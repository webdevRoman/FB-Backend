package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.exception.ValidationErrorException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.mapper.UserMapper;
import ru.rgrabelnikov.fbbackend.model.Role;
import ru.rgrabelnikov.fbbackend.model.UserEntity;
import ru.rgrabelnikov.fbbackend.repo.UserQuestionRepo;
import ru.rgrabelnikov.fbbackend.repo.UserRepo;
import ru.rgrabelnikov.fbbackend.service.UserService;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceBean implements UserService {

    private final UserRepo userRepo;

    private final UserQuestionRepo userQuestionRepo;

    private final UserMapper userMapper;

    private final CommonMapper commonMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepo.findByLogin(username)
                .map(userMapper::toContextUser);
    }

    @Override
    public Mono<UserEntity> findByLogin(String login) {
        return userRepo.findByLogin(login);
    }

    @Override
    public Flux<IdNameDto> findAllQuestions() {
        return userQuestionRepo.findAll()
                .map(uq -> commonMapper.toIdNameDto(uq.getId().toString(), uq.getQuestion()));
    }

    @Override
    public Mono<ResponseEntity<?>> create(UserRegistrationDto userRegistrationDto) {
        return Mono.just(userRegistrationDto)
                .doOnNext(dto -> log.info("Регистрация пользователя {}", dto.getLogin()))
                .flatMap(dto -> userRepo.findByLogin(userRegistrationDto.getLogin()))
                .hasElement()
                .flatMap(hasElement -> {
                    if (hasElement) {
                        return Mono.error(new ValidationErrorException(
                                List.of(commonMapper.toValidationErrorDto("login", "Логин не уникален"))
                        ));
                    } else {
                        UserEntity userEntity = new UserEntity();
                        userEntity.setId();
                        userEntity.setLogin(userRegistrationDto.getLogin());
                        userEntity.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
                        userEntity.setRole(Role.USER);
                        userEntity.setQuestionId(UUID.fromString(userRegistrationDto.getQuestionId()));
                        userEntity.setQuestionAnswer(passwordEncoder.encode(userRegistrationDto.getAnswer()));
                        return Mono.just(userEntity);
                    }
                })
                .flatMap(userRepo::save)
                .doOnNext(entity -> log.info("Пользователь {} зарегистрирован", entity.getLogin()))
                .map(jwtProvider::generateToken)
                .map(ResponseEntity::ok);
    }
}
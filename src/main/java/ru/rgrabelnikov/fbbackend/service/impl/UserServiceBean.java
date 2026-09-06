package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.domain.UserEntity;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.security.Role;
import ru.rgrabelnikov.fbbackend.dto.user.JwtDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserAuthDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserPasswordRecoveryDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.exception.ConflictException;
import ru.rgrabelnikov.fbbackend.exception.ForbiddenException;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.mapper.UserMapper;
import ru.rgrabelnikov.fbbackend.repo.IconRepo;
import ru.rgrabelnikov.fbbackend.repo.UserQuestionRepo;
import ru.rgrabelnikov.fbbackend.repo.UserRepo;
import ru.rgrabelnikov.fbbackend.service.UserService;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceBean implements UserService {

    private final UserRepo userRepo;
    private final UserQuestionRepo userQuestionRepo;
    private final IconRepo iconRepo;

    private final UserMapper userMapper;
    private final CommonMapper commonMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    public Mono<UserDetails> findByUsername(@NonNull final String username) {
        return userRepo.findByLogin(username)
                .map(userMapper::toContextUser);
    }

    @Override
    public Mono<ListWrapperDto<IdNameDto>> getUserQuestions() {
        return userQuestionRepo.findAll()
                .map(uq -> commonMapper.toIdNameDto(uq.getId(), uq.getQuestion()))
                .collectList()
                .map(commonMapper::toListWrapperDto);
    }

    @Override
    @Transactional
    public Mono<JwtDto> register(final UserRegistrationDto body) {
        log.info("Registering user {}", body.login());
        return userRepo.findByLogin(body.login())
                .hasElement()
                .<UserEntity>handle((hasElement, sink) -> {
                    if (hasElement) {
                        sink.error(new ConflictException(format("Login %s is not unique", body.login())));
                    } else {
                        final UserEntity userEntity = new UserEntity();
                        userEntity.setId();
                        userEntity.setLogin(body.login());
                        userEntity.setPassword(passwordEncoder.encode(body.password()));
                        userEntity.setRole(Role.USER);
                        userEntity.setQuestionId(body.questionId());
                        userEntity.setQuestionAnswer(passwordEncoder.encode(body.answer()));
                        sink.next(userEntity);
                    }
                })
                .flatMap(userRepo::save)
                .doOnNext(entity -> log.info("User {} registered", entity.getLogin()))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(entity -> createDefaultIcon(entity).then(Mono.just(entity)))
                .map(jwtProvider::generateToken)
                .map(userMapper::toJwtDto);
    }

    private Mono<IconEntity> createDefaultIcon(final UserEntity user) {
        byte[] bytes = null;
        try {
            final File file = ResourceUtils.getFile("classpath:files/default_icon.png");
            try (final InputStream in = new FileInputStream(file)) {
                bytes = in.readAllBytes();
            }
        } catch (Exception _) {
        }

        final IconEntity iconEntity = new IconEntity();
        iconEntity.setId();
        iconEntity.setUserId(user.getId());
        iconEntity.setIcon(bytes);
        iconEntity.setTooltip("Иконка");
        return iconRepo.save(iconEntity)
                .doOnNext(icon -> log.info("Created icon: {}", icon.getId()));
    }

    @Override
    public Mono<JwtDto> login(final UserAuthDto body) {
        log.info("Authorizing user {}", body.login());
        return getByLogin(body.login())
                .handle((userEntity, sink) -> {
                    if (passwordEncoder.matches(body.password(), userEntity.getPassword())) {
                        log.info("User {} authorized", userEntity.getLogin());
                        final String jwt = jwtProvider.generateToken(userEntity);
                        sink.next(userMapper.toJwtDto(jwt));
                        return;
                    }
                    sink.error(new ForbiddenException("Wrong password"));
                });
    }

    @Override
    @Transactional
    public Mono<JwtDto> recoverPassword(final UserPasswordRecoveryDto body) {
        log.info("Updating password of user {}", body.login());
        return getByLogin(body.login())
                .<UserEntity>handle((userEntity, sink) -> {
                    if (passwordEncoder.matches(body.answer(), userEntity.getQuestionAnswer())) {
                        log.info("Answer to secret question of user {} is correct", userEntity.getLogin());
                        sink.next(userEntity);
                        return;
                    }
                    sink.error(new ForbiddenException("Wrong answer to secret question"));
                })
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(body.password()));
                    return user;
                })
                .flatMap(userRepo::save)
                .doOnNext(entity -> log.info("Password of user {} is updated", entity.getLogin()))
                .map(jwtProvider::generateToken)
                .map(userMapper::toJwtDto);
    }

    private Mono<UserEntity> getByLogin(final String login) {
        return userRepo.findByLogin(login)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(UserEntity.class, "login = " + login))));
    }
}

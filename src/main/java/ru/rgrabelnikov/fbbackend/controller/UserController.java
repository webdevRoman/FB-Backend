package ru.rgrabelnikov.fbbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.api.UserApi;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.UserAuthDto;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.service.UserService;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class UserController implements UserApi {

    private final UserService service;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final CommonMapper commonMapper;

    @Override
    public Mono<ResponseEntity<List<IdNameDto>>> getSecretQuestions() {
        return service.findAllQuestions()
                .collectList()
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<?>> login(UserAuthDto body) {
        return Mono.just(body)
                .doOnNext(dto -> log.info("Авторизация пользователя {}", dto.getLogin()))
                .flatMap(userAuthDto -> service.findByLogin(userAuthDto.getLogin()))
                .map(userEntity -> passwordEncoder.matches(body.getPassword(), userEntity.getPassword())
                        ? ResponseEntity.ok(jwtProvider.generateToken(userEntity))
                        : commonMapper.toValidationErrorResponse("password", "Неверный пароль")
                )
                .defaultIfEmpty(commonMapper.toValidationErrorResponse("login", "Пользователь с указанным логином не найден"));
    }

    @Override
    public Mono<ResponseEntity<?>> register(Mono<UserRegistrationDto> body) {
        return body.flatMap(service::create);
    }
}
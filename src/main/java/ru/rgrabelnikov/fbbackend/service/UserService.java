package ru.rgrabelnikov.fbbackend.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.user.JwtDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserAuthDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserPasswordRecoveryDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserRegistrationDto;

public interface UserService extends ReactiveUserDetailsService {

    Mono<ListWrapperDto<IdNameDto>> getUserQuestions();

    Mono<JwtDto> register(UserRegistrationDto body);

    Mono<JwtDto> login(UserAuthDto body);

    Mono<JwtDto> recoverPassword(UserPasswordRecoveryDto body);
}

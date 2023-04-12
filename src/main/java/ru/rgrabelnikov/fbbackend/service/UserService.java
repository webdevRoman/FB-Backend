package ru.rgrabelnikov.fbbackend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.model.UserEntity;

public interface UserService extends ReactiveUserDetailsService {

    Mono<UserEntity> findByLogin(String login);

    Flux<IdNameDto> findAllQuestions();

    Mono<ResponseEntity<?>> create(UserRegistrationDto userRegistrationDto);
}
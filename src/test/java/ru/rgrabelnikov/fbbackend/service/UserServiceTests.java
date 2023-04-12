package ru.rgrabelnikov.fbbackend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.rgrabelnikov.fbbackend.UserFactory;
import ru.rgrabelnikov.fbbackend.dto.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.dto.ValidationErrorDto;
import ru.rgrabelnikov.fbbackend.exception.ValidationErrorException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.model.UserEntity;
import ru.rgrabelnikov.fbbackend.repo.UserRepo;
import ru.rgrabelnikov.fbbackend.service.impl.UserServiceBean;
import ru.rgrabelnikov.fbbackend.util.JwtProvider;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserServiceBean userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CommonMapper commonMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("Создание пользователя")
    public void createUser() {
        final UserRegistrationDto dto = UserFactory.createUserRegistrationDto();
        final UserEntity entity = UserFactory.createUserEntity();
        final String tokenValue = "token";

        Mockito.when(userRepo.findByLogin(UserFactory.LOGIN)).thenReturn(Mono.empty());
        Mockito.when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(Mono.just(entity));
        Mockito.when(passwordEncoder.encode(UserFactory.PASSWORD)).thenReturn(UserFactory.PASSWORD);
        Mockito.when(passwordEncoder.encode(UserFactory.ANSWER)).thenReturn(UserFactory.ANSWER);
        Mockito.when(jwtProvider.generateToken(entity)).thenReturn(tokenValue);

        Mono<ResponseEntity<?>> tokenMono = userService.create(dto);
        StepVerifier
                .create(tokenMono)
                .consumeNextWith(token -> {
                    assertInstanceOf(String.class, token.getBody());
                    assertEquals(token.getBody(), tokenValue);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Создание неуникального пользователя")
    public void createUserNotUnique() {
        final UserRegistrationDto dto = UserFactory.createUserRegistrationDto();
        final UserEntity entity = UserFactory.createUserEntity();

        final String errField = "login";
        final String errText = "Логин не уникален";
        final ValidationErrorDto errorDto = new ValidationErrorDto();
        errorDto.setField(errField);
        errorDto.setError(errText);

        Mockito.when(userRepo.findByLogin(UserFactory.LOGIN)).thenReturn(Mono.just(entity));
        Mockito.when(commonMapper.toValidationErrorDto(errField, errText)).thenReturn(errorDto);

        Mono<ResponseEntity<?>> tokenMono = userService.create(dto);
        StepVerifier
                .create(tokenMono)
                .consumeErrorWith(ex -> {
                    assertInstanceOf(ValidationErrorException.class, ex);
                    assertNotNull(((ValidationErrorException) ex).getErrors());
                    assertEquals(((ValidationErrorException) ex).getErrors().size(), 1);
                    ValidationErrorDto errDto = ((ValidationErrorException) ex).getErrors().get(0);
                    assertEquals(errDto.getField(), errField);
                    assertEquals(errDto.getError(), errText);
                })
                .verify();
    }
}
package ru.rgrabelnikov.fbbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import ru.rgrabelnikov.fbbackend.AbstractIntegrationTest;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserAuthDto;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.rgrabelnikov.fbbackend.testutil.TestUtils.getAsObject;

public class UserControllerTests extends AbstractIntegrationTest {

    @Test
    public void getUserQuestions() {
        webClient
                .get()
                .uri("/users/registration/meta")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ListWrapperDto<IdNameDto>>() {
                })
                .value(rs -> {
                    assertThat(rs.items()).isNotNull();
                    assertThat(rs.items()).hasSize(15);
                });
    }

//    @Test
//    public void login() {
//        final UserAuthDto dto = UserFactory.createUserAuthDto();
//        final UserEntity entity = UserFactory.createUserEntity();
//        final String tokenValue = "token";
//
//        final String errField = "login";
//        final String errText = "Пользователь с указанным логином не найден";
//        final ValidationErrorDto errorDto = new ValidationErrorDto(errField, errText);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse = ResponseEntity.badRequest()
//                .body(List.of(errorDto));
//
//        Mockito.when(userService.getByLogin(UserFactory.LOGIN)).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.matches(Mockito.eq(UserFactory.PASSWORD), Mockito.anyString())).thenReturn(true);
//        Mockito.when(jwtProvider.generateToken(Mockito.any(UserEntity.class))).thenReturn(tokenValue);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField, errText)).thenReturn(errResponse);
//
//        webClient
//                .post()
//                .uri("/api/user/login")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(String.class)
//                .isEqualTo(tokenValue);
//    }

    @Test
    public void login_userNotFound() {
        final UserAuthDto rq = getAsObject("json/rq/user/UserAuthDto.json", UserAuthDto.class);
        webClient
                .post()
                .uri("/users/login")
                .bodyValue(rq)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ServerErrorDto.class)
                .value(rs -> {
                    final ServerErrorDto expectedRs = getAsObject("json/rs/user/ServerErrorDto.json", ServerErrorDto.class);
                    assertThat(rs)
                            .usingRecursiveComparison()
                            .ignoringFields("timestamp", "requestId")
                            .isEqualTo(expectedRs);
                });
    }

//    @Test
//    @DisplayName("Авторизация - неверный пароль")
//    public void loginWrongPassword() {
//        final UserAuthDto dto = UserFactory.createUserAuthDto();
//        final UserEntity entity = UserFactory.createUserEntity();
//
//        final String errField1 = "password";
//        final String errText1 = "Неверный пароль";
//        final ValidationErrorDto errorDto1 = new ValidationErrorDto(errField1, errText1);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse1 = ResponseEntity.badRequest()
//                .body(List.of(errorDto1));
//
//        final String errField2 = "login";
//        final String errText2 = "Пользователь с указанным логином не найден";
//        final ValidationErrorDto errorDto2 = new ValidationErrorDto(errField2, errText2);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse2 = ResponseEntity.badRequest()
//                .body(List.of(errorDto2));
//
//        Mockito.when(userService.getByLogin(UserFactory.LOGIN)).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.matches(Mockito.eq(UserFactory.PASSWORD), Mockito.anyString())).thenReturn(false);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField1, errText1)).thenReturn(errResponse1);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField2, errText2)).thenReturn(errResponse2);
//
//        webClient
//                .post()
//                .uri("/api/user/login")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isBadRequest()
//                .expectBodyList(ValidationErrorDto.class)
//                .value(errList -> {
//                    assertEquals(1, errList.size());
//                    ValidationErrorDto errDto = errList.get(0);
//                    assertEquals(errField1, errDto.field());
//                    assertEquals(errText1, errDto.error());
//                });
//    }
//
//    @Test
//    @DisplayName("Регистрация")
//    public void register() {
//        final UserRegistrationDto dto = UserFactory.createUserRegistrationDto();
//        final String tokenValue = "token";
//
//        Mockito.when(userService.create(dto)).thenReturn(Mono.just(ResponseEntity.ok(tokenValue)));
//
//        webClient
//                .post()
//                .uri("/api/user/register")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(String.class)
//                .isEqualTo(tokenValue);
//    }
//
//    @Test
//    @DisplayName("Проверка секретного вопроса для восстановления пароля")
//    public void recoverPasswordCheck() {
//        final UserPasswordRecoveryDto dto = UserFactory.createUserRecoverCheckDto();
//        final UserEntity entity = UserFactory.createUserEntity();
//        final String tokenValue = "token";
//
//        final String errField1 = "login";
//        final String errText1 = "Пользователь с указанным логином не найден";
//        final ValidationErrorDto errorDto1 = new ValidationErrorDto(errField1, errText1);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse1 = ResponseEntity.badRequest()
//                .body(List.of(errorDto1));
//
//        final String errField2 = "answer";
//        final String errText2 = "Неверный ответ";
//        final ValidationErrorDto errorDto2 = new ValidationErrorDto(errField2, errText2);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse2 = ResponseEntity.badRequest()
//                .body(List.of(errorDto2));
//
//        Mockito.when(userService.getByLogin(UserFactory.LOGIN)).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.matches(Mockito.eq(UserFactory.ANSWER), Mockito.anyString())).thenReturn(true);
//        Mockito.when(jwtProvider.generateToken(Mockito.any(UserEntity.class))).thenReturn(tokenValue);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField1, errText1)).thenReturn(errResponse1);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField2, errText2)).thenReturn(errResponse2);
//
//        webClient
//                .post()
//                .uri("/api/user/recover/check")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(String.class)
//                .isEqualTo(tokenValue);
//    }
//
//    @Test
//    @DisplayName("Проверка секретного вопроса для восстановления пароля не пройдена")
//    public void recoverPasswordCheckWrongAnswer() {
//        final UserPasswordRecoveryDto dto = UserFactory.createUserRecoverCheckDto();
//        final UserEntity entity = UserFactory.createUserEntity();
//        final String tokenValue = "token";
//
//        final String errField1 = "login";
//        final String errText1 = "Пользователь с указанным логином не найден";
//        final ValidationErrorDto errorDto1 = new ValidationErrorDto(errField1, errText1);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse1 = ResponseEntity.badRequest()
//                .body(List.of(errorDto1));
//
//        final String errField2 = "answer";
//        final String errText2 = "Неверный ответ";
//        final ValidationErrorDto errorDto2 = new ValidationErrorDto(errField2, errText2);
//        final ResponseEntity<List<ValidationErrorDto>> errResponse2 = ResponseEntity.badRequest()
//                .body(List.of(errorDto2));
//
//        Mockito.when(userService.getByLogin(UserFactory.LOGIN)).thenReturn(Mono.just(entity));
//        Mockito.when(passwordEncoder.matches(Mockito.eq(UserFactory.ANSWER), Mockito.anyString())).thenReturn(false);
//        Mockito.when(jwtProvider.generateToken(Mockito.any(UserEntity.class))).thenReturn(tokenValue);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField1, errText1)).thenReturn(errResponse1);
//        Mockito.when(commonMapper.toValidationErrorResponse(errField2, errText2)).thenReturn(errResponse2);
//
//        webClient
//                .post()
//                .uri("/api/user/recover/check")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isBadRequest()
//                .expectBodyList(ValidationErrorDto.class)
//                .value(errList -> {
//                    assertEquals(1, errList.size());
//                    ValidationErrorDto errDto = errList.get(0);
//                    assertEquals(errField2, errDto.field());
//                    assertEquals(errText2, errDto.error());
//                });
//    }
//
//    @Test
//    @DisplayName("Восстановление пароля пользователя")
//    public void recoverPassword() {
//        final UserAuthDto dto = UserFactory.createUserAuthDto();
//        final String tokenValue = "token";
//
//        Mockito.when(userService.recoverPassword(dto)).thenReturn(Mono.just(ResponseEntity.ok(tokenValue)));
//
//        webClient
//                .put()
//                .uri("/api/user/recover")
//                .bodyValue(dto)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody(String.class)
//                .isEqualTo(tokenValue);
//    }
}

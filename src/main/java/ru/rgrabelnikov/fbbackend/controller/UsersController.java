package ru.rgrabelnikov.fbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.user.JwtDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserAuthDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserPasswordRecoveryDto;
import ru.rgrabelnikov.fbbackend.dto.user.UserRegistrationDto;
import ru.rgrabelnikov.fbbackend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = {"application/json"})
@ApiResponse(responseCode = "200", description = "Запрос успешно обработан")
@ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "404", description = "Данные не найдены",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@Tag(name = "Users", description = "Работа с пользователями")
public class UsersController {

    private final UserService service;

    @GetMapping("/registration/meta")
    @Operation(summary = "Получение списка секретных вопросов")
    public Mono<ListWrapperDto<IdNameDto>> getUserQuestions() {
        return service.getUserQuestions();
    }

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя")
    public Mono<JwtDto> register(@Valid @RequestBody UserRegistrationDto body) {
        return service.register(body);
    }

    @PostMapping("/login")
    @Operation(summary = "Авторизация")
    public Mono<JwtDto> login(@Valid @RequestBody UserAuthDto body) {
        return service.login(body);
    }

    @PutMapping("/recovery")
    @Operation(summary = "Восстановление пароля")
    public Mono<JwtDto> recoverPassword(@Valid @RequestBody UserPasswordRecoveryDto body) {
        return service.recoverPassword(body);
    }
}

package ru.rgrabelnikov.fbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountCreateDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountDto;
import ru.rgrabelnikov.fbbackend.service.AccountService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/accounts", produces = {"application/json"})
@ApiResponse(responseCode = "200", description = "Запрос успешно обработан")
@ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "401", description = "Ошибка авторизации",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "403", description = "Нет полномочий",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "404", description = "Данные не найдены",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@Tag(name = "Accounts", description = "Работа со счетами")
public class AccountsController {

    private final AccountService service;

    @GetMapping
    @Operation(summary = "Получение счетов")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ListWrapperDto<AccountDto>> getAll() {
        return service.getAllUserAccounts();
    }

    @PostMapping
    @Operation(summary = "Создание счета")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<AccountDto> create(@Valid @RequestBody final AccountCreateDto body) {
        return service.createUserAccount(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление счета")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<AccountDto> update(@PathVariable final UUID id, @Valid @RequestBody final AccountCreateDto body) {
        return service.updateUserAccount(id, body);
    }
}

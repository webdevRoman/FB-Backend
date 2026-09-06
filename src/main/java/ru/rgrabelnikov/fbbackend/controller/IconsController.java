package ru.rgrabelnikov.fbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import ru.rgrabelnikov.fbbackend.dto.icon.IconCreateDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;
import ru.rgrabelnikov.fbbackend.service.IconService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/icons", produces = {"application/json"})
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
@Tag(name = "Icons", description = "Работа с иконками")
public class IconsController {

    private final IconService service;

    @GetMapping
    @Operation(summary = "Получение иконок")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ListWrapperDto<IconDto>> getAll() {
        return service.getAllUserIcons();
    }

    @PostMapping
    @Operation(summary = "Добавление иконки")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<IconDto> create(@Valid @RequestBody IconCreateDto body) {
        return service.createUserIcon(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Редактирование иконки")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<IconDto> update(
            @NotNull
            @PathVariable
            UUID id,

            @Valid
            @RequestBody
            IconCreateDto body
    ) {
        return service.updateUserIcon(id, body);
    }
}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryCreateDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryUpdateDto;
import ru.rgrabelnikov.fbbackend.service.CategoryService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/categories", produces = {"application/json"})
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
@Tag(name = "Categories", description = "Работа с категориями")
public class CategoriesController {

    private final CategoryService service;

    @GetMapping
    @Operation(summary = "Получение категорий")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ListWrapperDto<CategoryDto>> getAll(@RequestParam("income") final Boolean income) {
        return service.getAllUserCategories(income);
    }

    @PostMapping
    @Operation(summary = "Создание категории")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<CategoryDto> create(@Valid @RequestBody final CategoryCreateDto body) {
        return service.createUserCategory(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление категории")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<CategoryDto> update(@PathVariable final UUID id, @Valid @RequestBody final CategoryUpdateDto body) {
        return service.updateUserCategory(id, body);
    }
}

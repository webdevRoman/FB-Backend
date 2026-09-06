package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

@Schema(description = "Обёртка DTO списков")
public record ListWrapperDto<T>(
        @Valid
        @Schema(description = "Список элементов")
        List<T> items
) {
}

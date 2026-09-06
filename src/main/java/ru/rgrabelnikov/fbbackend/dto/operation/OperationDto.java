package ru.rgrabelnikov.fbbackend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryViewDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO операции")
public record OperationDto(
        @Schema(description = "ID") UUID id,
        @Schema(description = "Сумма") BigDecimal amount,
        @Schema(description = "Дата") LocalDate date,
        @Schema(description = "Описание") String description,
        @Schema(description = "категория") CategoryViewDto category
) {
}

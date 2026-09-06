package ru.rgrabelnikov.fbbackend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "DTO для изменения операции")
public record OperationUpdateDto(

        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 2)
        @Schema(description = "Сумма")
        BigDecimal amount,

        @NotNull
        @Schema(description = "Дата")
        LocalDate date,

        @Schema(description = "Описание")
        String description,

        @NotNull
        @Schema(description = "ID категории")
        UUID categoryId
) {
}

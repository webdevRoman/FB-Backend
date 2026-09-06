package ru.rgrabelnikov.fbbackend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

@Schema(description = "DTO для импорта операций")
public record OperationImportDto(

        @NotNull
        @PositiveOrZero
        @Schema(description = "Номер колонки \"Дата\"")
        Integer dateColumnIndex,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Номер колонки \"Сумма\"")
        Integer amountColumnIndex,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Номер колонки \"Описание\"")
        Integer descriptionColumnIndex,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Номер колонки \"Категория\"")
        Integer categoryColumnIndex,

        @NotBlank
        @Schema(description = "Формат даты")
        String dateFormat,

        @NotNull
        @Schema(description = "ID счёта")
        UUID accountId
) {
}

package ru.rgrabelnikov.fbbackend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO для экспорта операций")
public record OperationExportDto(
        @NotNull
        @Schema(description = "ID счёта")
        UUID accountId
) {
}

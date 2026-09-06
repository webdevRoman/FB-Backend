package ru.rgrabelnikov.fbbackend.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO создания счёта")
public record AccountCreateDto(

        @NotBlank
        @Schema(description = "Наименование")
        String name,

        @NotNull
        @Schema(description = "ID иконки")
        UUID iconId
) {
}

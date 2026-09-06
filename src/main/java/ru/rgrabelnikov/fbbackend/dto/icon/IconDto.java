package ru.rgrabelnikov.fbbackend.dto.icon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO иконки")
public record IconDto(
        @Schema(description = "ID") UUID id,
        @Schema(description = "Иконка") byte[] icon,
        @Schema(description = "Тултип") String tooltip
) {
}

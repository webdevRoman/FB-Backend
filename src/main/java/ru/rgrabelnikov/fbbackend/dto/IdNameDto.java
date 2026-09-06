package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO с id и названием")
public record IdNameDto(
        @Schema(description = "ID") UUID id,
        @Schema(description = "Название") String name
) {
}

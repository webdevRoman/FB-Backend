package ru.rgrabelnikov.fbbackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO с JWT-токеном")
public record JwtDto(
        @NotBlank
        @Schema(description = "JWT-токен")
        String jwt
) {
}

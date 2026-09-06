package ru.rgrabelnikov.fbbackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для авторизации")
public record UserAuthDto(

        @NotBlank
        @Schema(description = "Логин")
        String login,

        @NotBlank
        @Schema(description = "Пароль")
        String password
) {
}

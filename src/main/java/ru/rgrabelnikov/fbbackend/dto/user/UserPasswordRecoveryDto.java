package ru.rgrabelnikov.fbbackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO для восстановления пароля")
public record UserPasswordRecoveryDto(

        @NotBlank
        @Schema(description = "Логин")
        String login,

        @NotBlank
        @Schema(description = "Ответ на секретный вопрос")
        String answer,

        @NotBlank
        @Schema(description = "Пароль")
        String password
) {
}

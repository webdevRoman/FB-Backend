package ru.rgrabelnikov.fbbackend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static ru.rgrabelnikov.fbbackend.util.ValidatorConstants.MAX_LENGTH_50;

@Schema(description = "DTO для регистрации")
public record UserRegistrationDto(

        @NotBlank
        @Size(max = MAX_LENGTH_50)
        @Schema(description = "Логин")
        String login,

        @NotBlank
        @Size(max = MAX_LENGTH_50)
        @Schema(description = "Пароль")
        String password,

        @NotNull
        @Schema(description = "ID вопроса")
        UUID questionId,

        @NotBlank
        @Size(max = MAX_LENGTH_50)
        @Schema(description = "Ответ")
        String answer
) {
}

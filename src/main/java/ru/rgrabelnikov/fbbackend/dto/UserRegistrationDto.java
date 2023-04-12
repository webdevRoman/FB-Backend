package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.rgrabelnikov.fbbackend.util.ValidatorConstants;

import java.io.Serializable;

/**
 * Дто для регистрации
 */
@Data
@Schema(description = "Дто для регистрации")
@Validated
public class UserRegistrationDto implements Serializable {

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    @Size(max = ValidatorConstants.LENGTH_50, message = ValidatorConstants.TEXT_MAX_LENGTH_50)
    private String login = null;

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    @Size(max = ValidatorConstants.LENGTH_50, message = ValidatorConstants.TEXT_MAX_LENGTH_50)
    private String password = null;

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    private String questionId = null;

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    @Size(max = ValidatorConstants.LENGTH_50, message = ValidatorConstants.TEXT_MAX_LENGTH_50)
    private String answer = null;
}
package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.rgrabelnikov.fbbackend.util.ValidatorConstants;

import java.io.Serializable;

/**
 * Дто для авторизации
 */
@Data
@Schema(description = "Дто для авторизации")
@Validated
public class UserAuthDto implements Serializable {

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    private String login = null;

    @NotBlank(message = ValidatorConstants.TEXT_REQUIRED)
    private String password = null;
}
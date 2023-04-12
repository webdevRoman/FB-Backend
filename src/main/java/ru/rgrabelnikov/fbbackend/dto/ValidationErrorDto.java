package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * Дто ошибки валидации
 */
@Data
@Schema(description = "Дто ошибки валидации")
@Validated
public class ValidationErrorDto implements Serializable {

    private String field = null;

    private String error = null;
}
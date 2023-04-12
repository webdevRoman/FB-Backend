package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * Дто ошибки на сервере
 */
@Data
@Schema(description = "Дто ошибки на сервере")
@Validated
public class ServerErrorDto implements Serializable {

    private String message = null;

    private String stacktrace = null;
}
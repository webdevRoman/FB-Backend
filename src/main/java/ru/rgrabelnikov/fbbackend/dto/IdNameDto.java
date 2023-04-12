package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

/**
 * Дто с id и названием
 */
@Data
@Schema(description = "Дто с id и названием")
@Validated
public class IdNameDto implements Serializable {

    private String id = null;

    private String name = null;
}
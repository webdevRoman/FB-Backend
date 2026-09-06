package ru.rgrabelnikov.fbbackend.dto.icon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * DTO создания иконки
 * @param icon    иконка
 * @param tooltip тултип
 */
@Schema(description = "DTO создания иконки")
public record IconCreateDto(

        @NotEmpty
        byte[] icon,

        String tooltip
) {
}

package ru.rgrabelnikov.fbbackend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "DTO изменения категории")
public record CategoryUpdateDto(

        @NotBlank
        @Schema(description = "Наименование")
        String name,

        @NotNull
        @Schema(description = "ID иконки")
        UUID iconId,

        @Schema(description = "ID родительской категории")
        UUID parentId
) {
}

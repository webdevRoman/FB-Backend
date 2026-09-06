package ru.rgrabelnikov.fbbackend.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;

import java.util.UUID;

@Schema(description = "DTO категории")
public record CategoryViewDto(
        @Schema(description = "ID") UUID id,
        @Schema(description = "Доход") Boolean income,
        @Schema(description = "Наименование") String name,
        @Schema(description = "Иконка") IconDto icon
) {
}

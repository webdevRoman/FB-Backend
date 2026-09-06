package ru.rgrabelnikov.fbbackend.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;

import java.util.UUID;

@Schema(description = "DTO счёта")
public record AccountDto(
        @Schema(description = "ID") UUID id,
        @Schema(description = "Наименование") String name,
        @Schema(description = "Иконка") IconDto icon
) {
}

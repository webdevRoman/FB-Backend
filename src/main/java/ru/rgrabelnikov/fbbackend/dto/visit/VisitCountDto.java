package ru.rgrabelnikov.fbbackend.dto.visit;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Schema(description = "DTO счётчика посещений")
@JsonInclude(NON_NULL)
public record VisitCountDto(
        @Schema(description = "Количество посещений") Long count,
        @Schema(description = "Источник данных: cache либо db") String source,
        @Schema(description = "ID созданного посещения") UUID lastId
) {
}

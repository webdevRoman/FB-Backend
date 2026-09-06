package ru.rgrabelnikov.fbbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;

@Schema(description = "Ошибка на сервере")
public record ServerErrorDto(
        @Schema(description = "Время возникновения ошибки") ZonedDateTime timestamp,
        @Schema(description = "URL") String path,
        @Schema(description = "Код") Integer status,
        @Schema(description = "Статус") String error,
        @Schema(description = "ID запроса") String requestId,
        @Schema(description = "Описание ошибки") String message,
        @Schema(description = "Полное имя класса исключения") String exception,
        @Schema(description = "Данные валидации") List<ValidationDataDto> errors
) {

    @Schema(description = "Ошибка валидации")
    public record ValidationDataDto(
            @Schema(description = "Имя параметра") String objectName,
            @Schema(description = "Имя поля") String field,
            @Schema(description = "Значение, не прошедшее валидацию") Object rejectedValue,
            @Schema(description = "Коды поля") String[] codes,
            @Schema(description = "Аргументы") ValidationDataDto[] arguments,
            @Schema(description = "Сообщение") String defaultMessage,
            @Schema(description = "Признак ошибки несовпадения типов") Boolean bindingFailure,
            @Schema(description = "Код ошибки") String code
    ) {
    }
}

package ru.rgrabelnikov.fbbackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.ValidationErrorDto;
import ru.rgrabelnikov.fbbackend.exception.ValidationErrorException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class HttpExceptionHandler {

    private final CommonMapper commonMapper;

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationErrors(WebExchangeBindException ex) {
        List<ValidationErrorDto> validationErrors = ex.getBindingResult().getAllErrors().stream()
                .filter(err -> err instanceof FieldError)
                .map(err -> commonMapper.toValidationErrorDto(((FieldError) err).getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        if (!validationErrors.isEmpty()) {
            log.error(
                    "Ошибки валидации сообщения {}:\n\t{}",
                    ex.getBindingResult().getObjectName(),
                    validationErrors.stream()
                            .map(err -> err.getField() + ": " + err.getError())
                            .collect(Collectors.joining("\n\t"))
            );
            return commonMapper.toValidationErrorResponse(validationErrors);
        }

        return null;
    }

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleValidationErrors(ValidationErrorException ex) {
        log.error(
                "Ошибки валидации:\n\t{}",
                ex.getErrors().stream()
                        .map(err -> err.getField() + ": " + err.getError())
                        .collect(Collectors.joining("\n\t"))
        );
        return commonMapper.toValidationErrorResponse(ex.getErrors());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerErrorDto> handleServerErrors(Exception ex) {
        log.error("Ошибка на сервере", ex);
        return commonMapper.toServerErrorResponse(ex);
    }
}
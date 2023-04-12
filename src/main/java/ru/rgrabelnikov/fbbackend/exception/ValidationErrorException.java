package ru.rgrabelnikov.fbbackend.exception;

import lombok.Getter;
import ru.rgrabelnikov.fbbackend.dto.ValidationErrorDto;

import java.util.List;

public class ValidationErrorException extends Exception {

    @Getter
    private List<ValidationErrorDto> errors;

    public ValidationErrorException(List<ValidationErrorDto> errors) {
        this.errors = errors;
    }
}
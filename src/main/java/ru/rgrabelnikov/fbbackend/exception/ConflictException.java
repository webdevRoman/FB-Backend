package ru.rgrabelnikov.fbbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(code = CONFLICT)
public class ConflictException extends AbstractException {

    public ConflictException(final String message) {
        super(message);
    }
}

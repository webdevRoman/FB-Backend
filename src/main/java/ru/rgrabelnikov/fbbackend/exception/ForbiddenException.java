package ru.rgrabelnikov.fbbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(code = FORBIDDEN)
public class ForbiddenException extends AbstractException {

    public ForbiddenException(final String message) {
        super(message);
    }
}

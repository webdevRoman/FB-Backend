package ru.rgrabelnikov.fbbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@ResponseStatus(code = UNAUTHORIZED)
public class UnauthorizedException extends AbstractException {

    public UnauthorizedException(final String message) {
        super(message);
    }
}

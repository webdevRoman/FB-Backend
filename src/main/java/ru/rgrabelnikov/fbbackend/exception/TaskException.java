package ru.rgrabelnikov.fbbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(code = INTERNAL_SERVER_ERROR)
public class TaskException extends AbstractException {

    public TaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

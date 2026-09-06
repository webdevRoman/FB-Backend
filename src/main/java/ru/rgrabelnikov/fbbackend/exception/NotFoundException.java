package ru.rgrabelnikov.fbbackend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(code = NOT_FOUND)
public class NotFoundException extends AbstractException {

    public NotFoundException(final Class<?> entityClass, final String entitySelector) {
        super(String.format("Entity %s %s not found", entityClass.getSimpleName(), entitySelector));
    }
}

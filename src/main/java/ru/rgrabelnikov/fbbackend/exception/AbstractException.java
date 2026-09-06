package ru.rgrabelnikov.fbbackend.exception;

import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public abstract class AbstractException extends RuntimeException {

    protected final Logger log = getLogger();

    public AbstractException(final Throwable cause) {
        super(cause);
        log.warn("{}: {}", getClass().getSimpleName(), cause.getMessage(), this);
    }

    public AbstractException(final String message, final Throwable cause) {
        super(message, cause);
        log.warn("{}: {}", getClass().getSimpleName(), message, this);
    }

    public AbstractException(final String message) {
        super(message);
        log.warn("{}: {}", getClass().getSimpleName(), message, this);
    }
}

package com.aveng.wapp.service.exception;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Our base ApplicationException for all custom exceptions. Default log level is ERROR.
 * Default HTTP Status of the returned response is 500.
 * Will be handled by {@link com.aveng.wapp.service.exception.handler.ApplicationExceptionHandler}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ApplicationException extends RuntimeException {

    /**
     * Http status of the response
     */
    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    /**
     * Log level of the exception
     */
    private Level level = Level.ERROR;

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status.value();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status.value();
    }

    public ApplicationException(HttpStatus status, String message, Level level) {
        super(message);
        this.status = status.value();
        this.level = level;
    }

    public ApplicationException(HttpStatus status, String message, Level level, Throwable cause) {
        super(message, cause);
        this.status = status.value();
        this.level = level;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns a new exception with BAD_REQUEST HTTP status code that will be logged at INFO level
     *
     * @param s Error message
     * @return a new ApplicationException
     */
    public static ApplicationException getValidationException(String s) {
        return new ApplicationException(HttpStatus.BAD_REQUEST, s, Level.INFO);
    }

    /**
     * Returns a new exception with BAD_REQUEST HTTP status code that will be logged at INFO level
     *
     * @param s Error message
     * @param t Throwable to be chained to this exception
     * @return a new ApplicationException
     */
    public static ApplicationException getValidationException(String s, Throwable t) {
        return new ApplicationException(HttpStatus.BAD_REQUEST, s, Level.INFO, t);
    }

}

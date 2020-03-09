package com.aveng.wapp.service.exception.mapper;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.aveng.wapp.service.exception.ApplicationException;
import com.aveng.wapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles ALL {@link ApplicationException}s, logging and returning a response
 *
 * @author apaydin
 */
@Component
@Slf4j
public class ApplicationExceptionHandler {

    /**
     * Will log and map an {@link ApplicationException} and return a meaningful response.
     *
     * @param exception {@link ApplicationException} to be handled
     * @return an {@link ApiResponse} with meaningful error messages
     */
    public ResponseEntity<ApiResponse<String>> mapApplicationException(final ApplicationException exception) {

        String errorMessage = Objects.toString(exception.getMessage(), "OOPS! error");

        if (exception.getLevel() != null) {
            switch (exception.getLevel()) {
                case ERROR:
                    log.error(errorMessage, exception);
                    break;
                case WARN:
                    log.warn(errorMessage, exception);
                    break;
                case INFO:
                    log.info(errorMessage, exception);
                    break;
                case DEBUG:
                    log.debug(errorMessage, exception);
                    break;
                case TRACE:
                    log.trace(errorMessage, exception);
                    break;
            }
        } else {
            log.error(errorMessage, exception);
        }

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
            .message(errorMessage)
            .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(exception.getStatus()));
    }
}

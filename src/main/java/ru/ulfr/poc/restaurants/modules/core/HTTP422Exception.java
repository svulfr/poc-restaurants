package ru.ulfr.poc.restaurants.modules.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception
 * Thrown in case well-formed request is passed, but data passed in it is invalid (e.g. non-existing
 * restaurant).
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class HTTP422Exception extends RuntimeException {
    public HTTP422Exception(String message) {
        super(message);
    }
}

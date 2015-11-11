package ru.ulfr.poc.restaurants.modules.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class HTTP403Exception extends RuntimeException {
}

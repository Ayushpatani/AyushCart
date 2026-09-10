package com.ayushcart.exception;

/** Mapped to HTTP 400: the request is understood but breaks a business rule. */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

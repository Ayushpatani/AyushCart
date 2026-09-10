package com.ayushcart.exception;

/** Mapped to HTTP 409, e.g. registering an email that already exists. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

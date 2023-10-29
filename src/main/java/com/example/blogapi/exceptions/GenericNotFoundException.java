package com.example.blogapi.exceptions;

public class GenericNotFoundException extends RuntimeException {
    public GenericNotFoundException(String message) {
        super(message);
    }

    public GenericNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericNotFoundException(Throwable cause) {
        super(cause);
    }
}

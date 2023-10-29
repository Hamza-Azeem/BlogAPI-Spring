package com.example.blogapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;

@ControllerAdvice
public class BlogExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Error> notFoundHandler(GenericNotFoundException exception){
        Error error = new Error(exception.getMessage(), HttpStatus.NOT_FOUND, new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<Error> badRequestHandler(BadRequestException exception){
        Error error = new Error(exception.getMessage(), HttpStatus.BAD_REQUEST, new Timestamp(System.currentTimeMillis()));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}

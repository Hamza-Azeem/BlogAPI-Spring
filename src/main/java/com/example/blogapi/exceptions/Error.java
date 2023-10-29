package com.example.blogapi.exceptions;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public record Error(String message, HttpStatus code, Timestamp timestamp) {
}

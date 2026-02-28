package com.vivatech.taskmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth related
    USERNAME_ALREADY_EXISTS("Username already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("Email is already registered", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_EMAIL_FORMAT("Invalid email format", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED("Token has expired. Please login again", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("Token not found", HttpStatus.UNAUTHORIZED),

    // Task related
    TASK_NOT_FOUND("Task not found", HttpStatus.NOT_FOUND),
    TASK_ALREADY_APPROVED("Task is already approved", HttpStatus.BAD_REQUEST),
    TASK_ALREADY_REJECTED("Task is already rejected", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACTION("You are not authorized to perform this action", HttpStatus.FORBIDDEN),

    // Generic
    SOMETHING_WENT_WRONG("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
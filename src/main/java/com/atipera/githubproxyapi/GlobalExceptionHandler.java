package com.atipera.githubproxyapi;

import com.atipera.githubproxyapi.model.ErrorResponse;
import com.atipera.githubproxyapi.model.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(404, e.getMessage());
    }
}

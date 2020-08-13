package com.example.study.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class HttpConflictException extends RuntimeException {

    String messageError;

    public HttpConflictException(String messageError) {
        this.messageError = messageError;
    }
}

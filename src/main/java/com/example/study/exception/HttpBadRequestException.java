package com.example.study.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {

    String messageError;

    public HttpBadRequestException(String messageError) {
        this.messageError = messageError;
    }
}

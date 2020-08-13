package com.example.study.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HttpNotFoundException extends RuntimeException {

    @JsonProperty("error_description")
    String messageError;

    public HttpNotFoundException(String messageError) {
        super(messageError);
        this.messageError = messageError;
    }
}

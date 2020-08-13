package com.example.study.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {

    @JsonProperty("user_name")
    private String username;

    @JsonProperty("password")
    private String password;
}

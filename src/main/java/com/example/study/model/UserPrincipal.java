package com.example.study.model;

import lombok.Data;

import java.util.List;

@Data
public class UserPrincipal {


    private String userId;

    private String userName;

    private String userPassword;

    private List<String> authorities;
}

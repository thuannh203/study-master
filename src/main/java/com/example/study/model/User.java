package com.example.study.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @JsonIgnore
    @Column(name = "user_id")
    @NotNull
    private String userId;

    @JsonProperty("username")
    @Column(name = "user_name")
    @NotNull
    private String username;

    @JsonProperty("user_password")
    @Column(name = "user_password")
    @NotNull
    private String userPassword;

    @JsonProperty("role_name")
    @Column(name = "role_name")
    @NotNull
    private String roleName;
}

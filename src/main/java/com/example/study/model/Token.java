package com.example.study.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "t_token")
@Data
public class Token {

    @Id
    @JsonIgnore
    @Column(name = "token_id")
    @NotNull
    private String tokenId;

    @Column(name = "user_id")
    @NotNull
    private String userId;

    @Column(name = "access_token", length = 1000)
    private String token;

    @Column(name = "register_timestamp")
    private Long tokenExpDate;

    public Token(){
        tokenId = UUID.randomUUID().toString();
    }
}

package com.example.study.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

    Token findByToken(String token);

    Token findByUserId(String userId);
}

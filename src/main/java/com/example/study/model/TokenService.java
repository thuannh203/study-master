package com.example.study.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional
    public Token createToken(Token token){
        return tokenRepository.saveAndFlush(token);
    }

    public Token findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Token findByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public void deleteToken(Token token){
        tokenRepository.delete(token);
    }
}

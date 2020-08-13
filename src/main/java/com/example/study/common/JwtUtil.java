package com.example.study.common;

import com.example.study.model.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    private static final String USER = "user";
    private static final String SECRET = "daycaidaynaychinhlachukycuabandungdelorangoaidaynhenguyhiemchetnguoidayhihihi";

    public String generateToken(UserPrincipal user) {
        String token = null;
        try {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
            builder.claim(USER, user);
            builder.expirationTime(new Date(generateExpirationDate()));
            JWTClaimsSet claimsSet = builder.build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            JWSSigner signer = new MACSigner(SECRET.getBytes());
            signedJWT.sign(signer);
            token = signedJWT.serialize();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return token;
    }

    public Long generateExpirationDate() {
        return CommonTimestamp.currentTimestamp() + 864000000;
    }

    private JWTClaimsSet getClaimsFromToken(String token) {
        JWTClaimsSet claims = null;
        try {
            log.warn("2");
            SignedJWT signedJWT = SignedJWT.parse(token);
            log.warn("3:"+signedJWT.toString());
            JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
            log.warn("4:"+signedJWT.toString());
            if (signedJWT.verify(verifier)) {
                log.warn("5:"+signedJWT.toString());
                claims = signedJWT.getJWTClaimsSet();
            }
        } catch (ParseException | JOSEException e) {
            log.error(e.getMessage());
        }
        return claims;
    }

    public UserPrincipal getUserFromToken(String token) {
        UserPrincipal user = null;
        try {
            log.warn("1");
            JWTClaimsSet claims = getClaimsFromToken(token);
            log.warn("6");
            if (claims != null && isTokenExpired(claims)) {
                log.warn("7");
                JSONObject jsonObject = (JSONObject) claims.getClaim(USER);
                log.warn("8: "+jsonObject.toJSONString());
                user = new ObjectMapper().readValue(jsonObject.toJSONString(), UserPrincipal.class);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return user;
    }

    private Date getExpirationDateFromToken(JWTClaimsSet claims) {
        return claims != null ? claims.getExpirationTime() : new Date();
    }

    private boolean isTokenExpired(JWTClaimsSet claims) {
        return getExpirationDateFromToken(claims).after(new Date());
    }
}

package com.example.study.web;

import com.example.study.common.JwtUtil;
import com.example.study.model.Token;
import com.example.study.model.TokenService;
import com.example.study.model.User;
import com.example.study.model.UserPrincipal;
import com.example.study.model.UserService;
import com.google.inject.internal.util.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody User user){
        user.setUserId(UUID.randomUUID().toString());
        user.setUserPassword(new BCryptPasswordEncoder().encode(user.getUserPassword()));
        return userService.create(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        UserPrincipal userPrincipal = userService.findByUsername(loginRequest.getUsername());
        Optional.ofNullable(userPrincipal).ifPresent(u -> {
            log.warn("userPrincipalPassword:{}, userLoginPassword:{} ",
                    userPrincipal.getUserPassword(), loginRequest.getPassword());
        });
        if (null == userPrincipal || !new BCryptPasswordEncoder().matches(loginRequest.getPassword(), userPrincipal.getUserPassword().replace("2y", "2a"))) {
            Map<String, String> errorResponse = ImmutableMap.of("error_description", "Tài khoản hoặc mật khẩu không chính xác");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        Token token = tokenService.findByUserId(userPrincipal.getUserId());
        if(token != null && new Date(token.getTokenExpDate()).after(new Date())) {
            tokenService.deleteToken(token);
        }
        token = new Token();
        token.setToken(jwtUtil.generateToken(userPrincipal));
        token.setUserId(userPrincipal.getUserId());
        token.setTokenExpDate(jwtUtil.generateExpirationDate());
        tokenService.createToken(token);
        return ResponseEntity.ok(token);
    }
}

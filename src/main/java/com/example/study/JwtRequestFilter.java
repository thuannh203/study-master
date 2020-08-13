package com.example.study;

import com.example.study.common.JwtUtil;
import com.example.study.model.Token;
import com.example.study.model.TokenService;
import com.example.study.model.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService verificationTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        final  String method = request.getMethod();
        UserPrincipal user = null;
        Token token = null;
        log.warn("start doFilterInternal" + method);
        log.warn("authorizationHeader: "+ authorizationHeader);
        log.warn("HeaderName: "+ request.getHeaderNames());
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Token ")) {
            String jwt = authorizationHeader.substring(6);
            log.warn("jwt: " + jwt);
            user = jwtUtil.getUserFromToken(jwt);
            token = verificationTokenService.findByToken(jwt);
        }

        log.warn("user: " + user);

        if (null != user && null != token && new Date(token.getTokenExpDate()).after(new Date())) {
            log.warn("token Date: " + token.getTokenExpDate());
            Set<GrantedAuthority> authorities = new HashSet<>();
            user.getAuthorities().forEach(p -> authorities.add(new SimpleGrantedAuthority((String) p)));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        log.warn("Request: " + request.getRequestURI());
        log.warn("Response: " + response);
        filterChain.doFilter(request, response);
    }
}

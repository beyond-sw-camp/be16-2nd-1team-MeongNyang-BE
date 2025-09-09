package com.beyond.meongnyang.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JwtTokenFilter extends GenericFilter {

    @Value("${jwt.securityAt}")
    private String secretKey;

    private static final String BEARER = "Bearer ";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        try {
            // 공개 경로/OPTIONS는 바로 통과
            String uri = req.getRequestURI();
            if (isPublic(uri) || "OPTIONS".equalsIgnoreCase(req.getMethod())) {
                chain.doFilter(request, response);
                return;
            }

            // Authorization 헤더 없거나 Bearer 아님 → 통과(익명)
            String auth = req.getHeader("Authorization");
            if (auth == null || !auth.startsWith(BEARER)) {
                chain.doFilter(request, response);
                return;
            }

            // 토큰 파싱
            String token = auth.substring(BEARER.length()).trim();
            if (token.isEmpty()) {
                chain.doFilter(request, response);
                return;
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + String.valueOf(claims.get("role"))));

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // 토큰이 잘못됐거나 만료 등 → 인증만 안 세우고 계속 진행
            log.error("JwtTokenFilter error: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String uri) {
        return uri.startsWith("/users/login")
                || uri.startsWith("/users/sign")
                || uri.startsWith("/users/find/email")
                || uri.startsWith("/users/check-email")
                || uri.startsWith("/users/check-nickname")
                || uri.startsWith("/users/check-phone")
                || uri.startsWith("/users/verify-email")
                || uri.startsWith("/users/verify-email-check")
                || uri.startsWith("/users/signup-extra")
                || uri.startsWith("/users/token/refresh")
                || uri.startsWith("/users/logout")
                || uri.startsWith("/connect/");
    }
}

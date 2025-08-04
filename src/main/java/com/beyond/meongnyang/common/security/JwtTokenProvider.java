package com.beyond.meongnyang.common.security;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(UserRepository userRepository, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Value("${jwt.expirationAt}")
    private int expirationAt;
    @Value("${jwt.securityAt}")
    private String secretKeyAt;

    @Value("${jwt.expirationRt}")
    private int expirationRt;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    private Key secretATToken;
    private Key secretRtToken;

    @PostConstruct
    public void init() {
        secretATToken = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyAt), SignatureAlgorithm.HS512.getJcaName());
        secretRtToken = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyRt), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createAtToken(User user) {
        String email = user.getEmail();
        String role = user.getRole().toString();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt * 60 * 1000L))
                .signWith(secretATToken)
                .compact();
        return token;
    }

    public String createRtToken(User user) {
        String email = user.getEmail();
        String role = user.getRole().toString();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt * 60 * 1000L))
                .signWith(secretRtToken)
                .compact();
        redisTemplate.opsForValue().set(user.getEmail(), token);
        return token;
    }

    public User validateRefreshToken(String refreshToken) {
        //rt 토큰 검증
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretRtToken)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        String email = claims.getSubject();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));
        String redisRt = redisTemplate.opsForValue().get(user.getEmail());
        if(!refreshToken.equals(redisRt)){
            throw new IllegalArgumentException("rt 토큰이 일치하지 않습니다.");
        }
        return user;
    }
}


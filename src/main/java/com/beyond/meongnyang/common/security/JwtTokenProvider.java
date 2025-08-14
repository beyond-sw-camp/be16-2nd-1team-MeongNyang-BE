package com.beyond.meongnyang.common.security;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JwtTokenProvider {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(UserRepository userRepository, @Qualifier("rtInventory") RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Value("${jwt.expirationAt}")  private int expirationAt;
    @Value("${jwt.securityAt}")    private String secretKeyAt;
    @Value("${jwt.expirationRt}")  private int expirationRt;
    @Value("${jwt.secretKeyRt}")   private String secretKeyRt;

    private Key secretATToken;
    private Key secretRtToken;

    private static final String RT_PREFIX = "RT:";

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
        String rtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L))
                .signWith(secretRtToken)
                .compact();
        String key = RT_PREFIX + user.getEmail();
        redisTemplate.opsForValue().set(key, rtToken, expirationRt * 60 *1000L, TimeUnit.SECONDS);
        return rtToken;
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
        String redisRt = redisTemplate.opsForValue().get(RT_PREFIX + email);
        if(!refreshToken.equals(redisRt)){
            throw new IllegalArgumentException("rt 토큰이 일치하지 않습니다.");
        }
        return user;
    }
    public String reissueAt(String refreshToken) {
        User user = validateRefreshToken(refreshToken);
        return createAtToken(user);
    }

    public void revokeRefreshToken(String email) {
        redisTemplate.delete(RT_PREFIX + email);
    }

    public String createSignupTicket(String socialId, String email, String socialType, int minutes) {
        Date now = new Date();
        return Jwts.builder()
                .claim("social_id", socialId)
                .claim("email", email)
                .claim("social_type",  socialType) // "GOOGLE"|"KAKAO"
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt * 60 * 1000L))
                .signWith(secretATToken) //
                .compact();
    }

    public SignupTicket parseSignupTicket(String token) {
        Claims c = Jwts.parserBuilder().setSigningKey(secretATToken).build()
                .parseClaimsJws(token).getBody();
        return new SignupTicket(
                c.get("social_id", String.class),
                c.get("email", String.class),
                c.get("social_type",  String.class)
        );
    }
    public String getSubjectFromRefresh(String refreshToken) {
        Claims c = Jwts.parserBuilder().setSigningKey(secretRtToken).build()
                .parseClaimsJws(refreshToken).getBody();
        return c.getSubject();
    }

    public record SignupTicket(String socialId, String email, String socialType) {}
}


package com.beyond.meongnyang.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class EmailVerificationService {

    @Qualifier("emailCodeInventory")
    private final RedisTemplate<String, String>  emailCodeRedisTemplate;

    public EmailVerificationService(RedisTemplate<String, String> emailCodeRedisTemplate) {
        this.emailCodeRedisTemplate = emailCodeRedisTemplate;
    }

    // 인증 코드 생성 및 Redis 저장
    public String createAndSendCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        String key = "verify:" + email;

        // Redis에 저장 (5분 TTL)
        Long ttl = 5L;
        emailCodeRedisTemplate.opsForValue().set(key, code, ttl, TimeUnit.MINUTES);

        return code; // 메일 발송용
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String inputCode) {
        String key = "verify:" + email;
        String savedCode = emailCodeRedisTemplate.opsForValue().get(key);
        return savedCode != null && savedCode.equals(inputCode);
    }

    // 인증 코드 삭제
    public void deleteCode(String email) {
        emailCodeRedisTemplate.delete("verify:" + email);
    }

}

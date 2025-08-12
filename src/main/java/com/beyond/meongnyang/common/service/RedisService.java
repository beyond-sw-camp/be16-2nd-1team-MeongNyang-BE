package com.beyond.meongnyang.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Qualifier("rtInventoryObject")
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String key, String token, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, token, ttlSeconds, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? obj.toString() : null;
    }

    public void saveObject(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        return clazz.isInstance(obj) ? clazz.cast(obj) : null;
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }
}

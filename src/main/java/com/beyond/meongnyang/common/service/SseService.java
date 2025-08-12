package com.beyond.meongnyang.common.service;

import com.beyond.meongnyang.common.registry.SseEmitterRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Component
public class SseService implements MessageListener {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final RedisTemplate<String, String> ssePubSubTemplate;

    public SseService(SseEmitterRegistry sseEmitterRegistry, @Qualifier("ssePubSub") RedisTemplate<String, String> ssePubSubTemplate) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.ssePubSubTemplate = ssePubSubTemplate;
    }

    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(14400 * 60 * 1000L); // 10일 정도 emitter 유효기간
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        this.sseEmitterRegistry.registerEmitter(email, emitter);
        log.info("sse emitter register success.");
        try {
            emitter.send(SseEmitter.event().name("connected").data("연결 완료"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return emitter;
    }

    public void disconnect() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        this.sseEmitterRegistry.removeEmitter(email);
    }

    //
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // message : 실질적인 메세지가 담겨있는 객체
        // pattern : 채널명
        ObjectMapper objectMapper = new ObjectMapper();

    }
}

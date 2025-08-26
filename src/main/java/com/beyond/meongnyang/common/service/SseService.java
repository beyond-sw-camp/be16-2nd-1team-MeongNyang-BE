package com.beyond.meongnyang.common.service;

import com.beyond.meongnyang.common.dto.SseMessageRes;
import com.beyond.meongnyang.common.registry.SseEmitterRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final ObjectMapper objectMapper;

    public SseService(SseEmitterRegistry sseEmitterRegistry, @Qualifier("ssePubSub") RedisTemplate<String, String> ssePubSubTemplate, ObjectMapper objectMapper) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.ssePubSubTemplate = ssePubSubTemplate;
        this.objectMapper = objectMapper;
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
        String channel = new String(message.getChannel());
        String receiver = channel.split("_")[1];
        String eventName = channel.split("_")[0];

        SseEmitter sseEmitter = sseEmitterRegistry.getEmitter(receiver);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name(eventName).data(message.getBody()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void publishMessage(String event, String receiver, String data) {
//        ssePubSubTemplate.convertAndSend(event + "_" + receiver, data);

//        emmiter객체를 통해 메시지 전송
        SseEmitter sseEmitter = sseEmitterRegistry.getEmitter(receiver);
//        emitter객체가 현재 서버에 있으면, 직접 알림 발송. 그렇지 않으면, redis에 publish
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name(event).data(data));
                log.info("sse emitter publish success.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ssePubSubTemplate.convertAndSend(event + "_" + receiver, data);
        }
    }
}

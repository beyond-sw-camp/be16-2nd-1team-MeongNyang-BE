package com.beyond.meongnyang.common.registry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterRegistry {
    // ConcurrentHashMap은 Tread-safe한 map(동시성 이슈 발생 X)
    Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void registerEmitter(String email, SseEmitter emitter) {
        // 연결이 완료(종료)되었을 때
        emitter.onCompletion(() -> {
            emitterMap.remove(email);
            log.info("[SSE] 연결 종료: {}", email);
        });

        // 타임아웃 발생 시
        emitter.onTimeout(() -> {
            emitterMap.remove(email);
            log.info("[SSE] 타임아웃: {}", email);
            emitter.complete();
        });

        // 에러 발생 시
        emitter.onError((e) -> {
            emitterMap.remove(email);
            log.info("[SSE] 에러 발생: {}, {}", email, e.getMessage());
            emitter.completeWithError(e);
        });

        emitterMap.put(email, emitter);
    }

    public void removeEmitter(String email) {
        emitterMap.remove(email);
    }

    public SseEmitter getEmitter(String email) {
        return emitterMap.get(email);
    }
}

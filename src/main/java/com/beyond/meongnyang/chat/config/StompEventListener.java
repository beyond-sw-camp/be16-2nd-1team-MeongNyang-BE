package com.beyond.meongnyang.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 스프링과 stomp는 기본적으로 세션관리를 자동(내부적)으로 처리한다.
// 연결, 해제 이벤트를 기록, 연결된 세션 수를 실시간으로 확인할 목적으로 이벤트 리스너를 생성 => 로그, 디버깅 목적
@Slf4j
@Component
public class StompEventListener {
    private final Set<String> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<String>> participants = new ConcurrentHashMap<>();

    @EventListener
    public void connectHandler(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.add(accessor.getSessionId());
        logSession(true, accessor);
    }

    @EventListener
    public void disconnectHandler(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessions.remove(accessor.getSessionId());
        logSession(false, accessor);
    }

//    @EventListener
//    public void subscribeHandler(SessionSubscribeEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        participants.computeIfAbsent(accessor.getDestination().split("/")[2],(k) -> ConcurrentHashMap.newKeySet())
//                .add(accessor.getSessionId());
//        log.info(participants.toString());
//        log.info("subscribe roomid : "+accessor.getDestination().split("/")[2]);
//    }
//
//    @EventListener
//    public void unsubscribeHandler(SessionUnsubscribeEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        log.info("unsubscribe roomid : "+accessor.getDestination());
//
//        participants.computeIfAbsent(accessor.getDestination().split("/")[2],(k) -> ConcurrentHashMap.newKeySet())
//                .remove(accessor.getSessionId());
//    }

    private void logSession(Boolean isConnect, StompHeaderAccessor accessor) {
        log.info("{}connect sessionId={}", isConnect ? "" : "dis", accessor.getSessionId());
        log.info("total sessions={}", sessions.size());
    }
}

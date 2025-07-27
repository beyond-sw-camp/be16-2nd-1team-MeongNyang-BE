//package com.beyond.meongnyang.chat.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Component
//public class SimpleWebSocketHandler extends TextWebSocketHandler {
//    private final Set<WebSocketSession> webSocketSessions = ConcurrentHashMap.newKeySet();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        webSocketSessions.add(session);
//        log.info("Connected to web socket session: {}", session.getId());
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String msg = message.getPayload();
//        log.info("Received message: {}", msg);
//        for (WebSocketSession s : webSocketSessions) {
//            if (s.isOpen()) s.sendMessage(new TextMessage(msg));
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        webSocketSessions.remove(session);
//        log.info("Disconnected from web socket session: {}", session.getId());
//    }
//
//
//}

//package com.beyond.meongnyang.chat.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    private final SimpleWebSocketHandler webSocketHandler;
//
//    @Value("${cors.origin}")
//    private String origin;
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        // "/connect"로 websocket 연결 요청이 들어오면 websockethandler가 처리
//        registry.addHandler(webSocketHandler, "/connect")
//                .setAllowedOrigins(origin); // cors예외
//    }
//}

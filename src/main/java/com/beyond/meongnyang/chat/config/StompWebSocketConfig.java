package com.beyond.meongnyang.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Value("${cors.origin}")
    private String origin;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins(origin)
                .withSockJS(); // http의 엔드포인트를 사용해 ws통신을 할 수 있게 해주는 라이브러리인 sockJS를 통한 요청을 허용(한마디로 ws통신을 http의 /connect를 엔드포인트로 사용하겠다 인 듯?)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) { // 클라이언트에게 메세지를 받아서 특정 클라이언트들에게 전달해주는 브로커(현실에서의 그 브로커 같은 역할)
        // /publish/{roomId} 로 메세지를 발행한다.
        // /publish로 시작하는 url패턴으로 메세지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/publish");

        // /topic/{roomId} 로 메세지를 수신한다.
        //
        registry.enableSimpleBroker("/topic");

    }

    // 웹소켓(connect, subscribe, disconnect)등의 요청 시에는 http header등 http메세지를 넣어올 수 있고, 이를 interceptor를 통해 가로채 토큰등을 검증할 수 있음
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}

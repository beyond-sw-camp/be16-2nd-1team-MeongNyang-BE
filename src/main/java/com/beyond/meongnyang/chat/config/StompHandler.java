package com.beyond.meongnyang.chat.config;

import com.beyond.meongnyang.chat.service.ChatService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    @Value("${jwt.securityAt}")
    private String jwtSecurityAt;

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            log.info("connect 요청시 토큰 유효성 검증");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring("Bearer ".length());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecurityAt)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

//            log.info("토큰 검증 완료");
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = bearerToken.substring("Bearer ".length());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecurityAt)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            Long roomId = Long.parseLong(accessor.getDestination().split("/")[2]);
            chatService.validChatRoomParticipant(roomId, email);
        }

        return message;
    }
}

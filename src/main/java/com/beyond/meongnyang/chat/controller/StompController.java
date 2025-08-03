package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.service.ChatService;
import com.beyond.meongnyang.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
//    // 방법 1. MessageMapping 과 SendTo 한번에 처리
//    @MessageMapping("/{roomId}") // 클라이언트에서 특정 roomId로 메세지 발행시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId에 메세지를 발행하여 구독 중인 클라이언트에게 메세지 전송
//    // DestinationVariable : MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
//    public String sendMessage(@DestinationVariable Long roomId, String chatMessage) {
//        log.info(chatMessage);
//        return chatMessage;
//    }

    // 방법 2. MessageMapping 어노테이션만 활용
    // 추후 변경사항이 발생시 방법1에 비해 더욱 유연하게 개선 가능
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {
        log.info(chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageDto);
    }
}

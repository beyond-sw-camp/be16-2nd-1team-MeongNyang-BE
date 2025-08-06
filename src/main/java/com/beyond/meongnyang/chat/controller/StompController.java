package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;

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

    // TODO : 엔드포인트 변경 필요(보내고 받는 것 모두)
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageReq chatMessageReq) {
        log.info(chatMessageReq.getMessage());
        ChatMessageRes chatMessageRes = chatService.saveMessage(roomId, chatMessageReq);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageRes);
    }

    @MessageMapping("/chat-rooms/{roomId}/leave")
    public void leaveRoom(@DestinationVariable Long roomId) {
        ChatParticipantRemRes chatParticipantRemRes = chatService.leaveChatRoomAndRemoveIfEmpty(roomId);
        // TODO : 나갔다는 메세지 발행을 위한 dto 필요
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/leave", chatParticipantRemRes);
    }

    @MessageMapping("/chat-rooms/{roomId}/invite")
    public void inviteUsers(@DestinationVariable Long roomId, List<ChatParticipantAddReq> chatParticipantAddReqs) {
        List<ChatParticipantAddRes> chatParticipantAddResList = chatService.inviteUsers(roomId, chatParticipantAddReqs);
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/invite", chatParticipantAddResList);
    }
}

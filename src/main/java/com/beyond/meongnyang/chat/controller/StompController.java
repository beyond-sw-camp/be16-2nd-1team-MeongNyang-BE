package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.service.ChatRedisService;
import com.beyond.meongnyang.chat.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompController {

    private final ChatService chatService;
    private final ChatRedisService chatRedisService;

    @MessageMapping("/chat-rooms/{roomId}/chat-message")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageReq chatMessageReq) {
        ChatMessageRes res = chatService.saveMessage(roomId, chatMessageReq);
        chatRedisService.publishChatMessageToRedis(roomId, res);
    }

    @MessageMapping("/chat-rooms/{roomId}/invite")
    public void inviteUsers(@DestinationVariable Long roomId, List<ChatParticipantAddReq> chatParticipantAddReqs) {
        List<ChatParticipantAddRes> chatParticipantAddRes = chatService.inviteUsers(roomId, chatParticipantAddReqs);
        chatRedisService.publishInvitedUsersToRedis(roomId, chatParticipantAddRes);
    }

    @MessageMapping("/chat-rooms/{roomId}/leave")
    public void leaveRoom(@DestinationVariable Long roomId) {
        chatService.leaveChatRoomAndRemoveIfEmpty(roomId);
        chatRedisService.publishLeftUserToRedis(roomId);
    }

    @MessageMapping("/chat-rooms/{roomId}/online")
    public void online(@DestinationVariable Long roomId, String email) {
        chatRedisService.publishChatOnlineToRedis(roomId, email);
    }

    @MessageMapping("/chat-rooms/{roomId}/offline")
    public void offline(@DestinationVariable Long roomId) {
        chatService.readMessages(roomId);
        chatRedisService.publishChatOfflineToRedis(roomId);
    }
}

package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.ChatService;
import com.beyond.meongnyang.chat.dto.ChatRoomCreateReq;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 채팅방 개설
    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody ChatRoomCreateReq chatRoomCreateReq) {
        Long chatRoomId = chatService.createChatRoom(chatRoomCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonRes.ofSuccess(chatRoomId, HttpStatus.OK.value(), chatRoomCreateReq.getRoomName() + " 채팅방 개설")
        );
    }
}

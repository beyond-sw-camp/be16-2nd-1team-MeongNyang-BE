package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.service.ChatRedisService;
import com.beyond.meongnyang.chat.service.ChatService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRedisService chatRedisService;

    // 채팅방 개설
    @PostMapping("")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomCreateReq chatRoomCreateReq) {
        Long chatRoomId = chatService.createChatRoom(chatRoomCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonRes.ofSuccess(chatRoomId, HttpStatus.CREATED.value(), chatRoomCreateReq.getRoomName() + " 채팅방 개설")
        );
    }

    // 요청을 보낸 유저가 소속된 채팅방 목록 조회
    @GetMapping("")
    public ResponseEntity<?> getMyChatRooms() {
        List<ChatRoomSummaryRes> myChatRoomList = chatService.getMyChatRooms();
        return ResponseEntity.ok(
                CommonRes.ofSuccess(myChatRoomList, HttpStatus.OK.value(), "chat room list")
        );
    }

    // 메세지 목록 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long roomId) {
        List<ChatMessageRes> chatMessageResList = chatService.getChatMessages(roomId);

        return ResponseEntity.ok(
                CommonRes.ofSuccess(chatMessageResList, HttpStatus.OK.value(), "chat message list")
        );
    }

    // 참여자 추가
    @PostMapping("/{roomId}/participants")
    public ResponseEntity<?> inviteUsers(@PathVariable Long roomId, @RequestBody List<ChatParticipantAddReq> chatParticipantAddReqList) {
        List<ChatParticipantAddRes> chatParticipantAddResList = chatService.inviteUsers(roomId, chatParticipantAddReqList);
        chatRedisService.publishInvitedUsersToRedis(roomId, chatParticipantAddResList);
        return ResponseEntity.ok(
                CommonRes.ofSuccess(null, HttpStatus.OK.value(), "invited users")
        );
    }

    // 채팅방 나가기
    @DeleteMapping("/{roomId}/participants/me")
    public ResponseEntity<?> leaveChatRoom(@PathVariable Long roomId) {
        chatService.leaveChatRoomAndRemoveIfEmpty(roomId);
        chatRedisService.publishLeftUserToRedis(roomId);
        return ResponseEntity.ok(
                CommonRes.ofSuccess(null, HttpStatus.OK.value(), "left chat room")
        );
    }

    // 참여자 목록 조회
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<?> getChatParticipants(@PathVariable Long roomId) {
        List<ChatParticipantRes> chatParticipantResList = chatService.getChatParticipants(roomId);

        return ResponseEntity.ok(
                CommonRes.ofSuccess(chatParticipantResList, HttpStatus.OK.value(), "participant list")
        );
    }

    // 채팅방에 온라인인 유저 목록 조회
//    @GetMapping("/{roomId}/online-participants")
//    public ResponseEntity<?> getChatOnlineParticipants(@PathVariable Long roomId) {
//        chatRedisService.
//    }
}

package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.service.ChatService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

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

    // 참여자 추가
    @PostMapping("{roomId}/participants")
    public ResponseEntity<?> inviteUsers(@PathVariable Long roomId, @RequestBody List<ChatParticipantAddReq> chatParticipantAddReqList) {
        chatService.inviteUsers(roomId, chatParticipantAddReqList);
        return null;
    }

    // 메세지 목록 조회
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long roomId) {
        List<ChatMessageRes> chatMessageResList = chatService.getChatMessages(roomId);

        return ResponseEntity.ok(
                CommonRes.ofSuccess(chatMessageResList, HttpStatus.OK.value(), "chat message list")
        );
    }

    @PostMapping("/{roomId}/files")
    public ResponseEntity<?> uploadFiles(@PathVariable Long roomId, @RequestParam List<MultipartFile> files) {

        List<String> fileUrls = chatService.uploadFiles(roomId, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonRes.ofSuccess(fileUrls, HttpStatus.CREATED.value(), "upload files")
        );
    }
}

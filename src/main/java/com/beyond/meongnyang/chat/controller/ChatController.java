package com.beyond.meongnyang.chat.controller;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.service.ChatRedisService;
import com.beyond.meongnyang.chat.service.ChatService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final ChatRedisService chatRedisService;

    // 채팅방 개설
    @PostMapping("")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomCreateReq chatRoomCreateReq) {
        log.info("createChatRoom:{}", chatRoomCreateReq.getMarketPostId());
        ChatRoomSummaryRes chatRoomSummaryRes = chatService.createChatRoom(chatRoomCreateReq);
        chatRedisService.publishNewChatRoomToRedis(chatRoomSummaryRes);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonRes.ofSuccess(chatRoomSummaryRes, HttpStatus.CREATED.value(), chatRoomCreateReq.getRoomName() + " 채팅방 개설")
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
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> inviteUsers(@PathVariable Long roomId, @RequestBody List<ChatParticipantAddReq> chatParticipantAddReqList) {
        List<ChatParticipantAddRes> chatParticipantAddResList = chatService.inviteUsers(roomId, chatParticipantAddReqList);
        chatRedisService.publishInvitedUsersToRedis(roomId, chatParticipantAddResList);
        return ResponseEntity.ok(
                CommonRes.ofSuccess(null, HttpStatus.OK.value(), "invited users")
        );
    }

    // 채팅방 나가기
    @DeleteMapping("/{roomId}/participants/me")
    @PreAuthorize("@securityCheck.checkUserAccess()")
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
  
    @PostMapping("/{roomId}/files")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> uploadFiles(@PathVariable Long roomId, @RequestParam List<MultipartFile> files) {

        List<String> fileUrls = chatService.uploadFiles(roomId, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonRes.ofSuccess(fileUrls, HttpStatus.CREATED.value(), "upload files")
        );
    }

    @PatchMapping("/{roomId}/approval-status")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> updateIsPurchaseApproved(@PathVariable Long roomId) {
        Boolean isPurchaseApproved = chatService.updateIsPurchaseApproved(roomId);
        chatRedisService.publishApprovalStatusToRedis(roomId, isPurchaseApproved);
        return ResponseEntity.status(HttpStatus.OK).body(
                CommonRes.ofSuccess(isPurchaseApproved, HttpStatus.OK.value(), "approval status updated")
        );
    }
}

package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMedia;
import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRes {
    private Long id;
    private String message;
    private String senderEmail;
    private List<String> fileUrls;

    private Long chatRoomId;

    public static ChatMessageRes fromEntity(ChatMessage chatMessage) {
        return ChatMessageRes.builder()
                .message(chatMessage.getContent())
                .senderEmail(chatMessage.getUser().getEmail())
                .id(chatMessage.getId())
                .fileUrls(chatMessage.getChatMediaList().stream().map(ChatMedia::getUrl).toList())
                .chatRoomId(chatMessage.getChatRoom().getId())
                .build();
    }
}
package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMessage;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRes {
    private Long id;
    private String message;
    private String senderEmail;
//    private Integer readCount;

    public static ChatMessageRes fromEntity(ChatMessage chatMessage/*, Integer readCount*/) {
        return ChatMessageRes.builder()
                .message(chatMessage.getContent())
                .senderEmail(chatMessage.getUser().getEmail())
                .id(chatMessage.getId())
//                .readCount(readCount)
                .build();
    }
}

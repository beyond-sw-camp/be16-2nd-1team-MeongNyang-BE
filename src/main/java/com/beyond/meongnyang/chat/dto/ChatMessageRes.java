package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRes {
    private Long messageId;
    private String message;
    private String senderEmail;
    private Integer readCount;

    public static ChatMessageRes fromEntity(ChatMessage chatMessage, Integer readCount) {
        return ChatMessageRes.builder()
                .message(chatMessage.getContent())
                .senderEmail(chatMessage.getUser().getEmail())
                .messageId(chatMessage.getId())
                .readCount(readCount)
                .build();
    }
}

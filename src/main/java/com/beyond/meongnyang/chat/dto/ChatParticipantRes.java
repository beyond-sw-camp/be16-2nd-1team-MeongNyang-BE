package com.beyond.meongnyang.chat.dto;


import com.beyond.meongnyang.chat.entity.ChatParticipant;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipantRes {
    private Long id;
    private String email;
    private Long roomId;
    private Long lastReadMessageId;

    public static ChatParticipantRes fromEntity(ChatParticipant chatParticipant) {
        return ChatParticipantRes.builder()
                .id(chatParticipant.getId())
                .email(chatParticipant.getUser().getEmail())
                .roomId(chatParticipant.getChatRoom().getId())
                .lastReadMessageId(chatParticipant.getLastReadMessage() == null? 0 : chatParticipant.getLastReadMessage().getId())
                .build();
    }
}

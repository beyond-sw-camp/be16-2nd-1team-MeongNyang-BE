package com.beyond.meongnyang.chat.dto;


import com.beyond.meongnyang.chat.entity.ChatParticipant;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipantRes {
    private Long id;
    private Long userId;
    private Long roomId;

    public static ChatParticipantRes fromEntity(ChatParticipant chatParticipant) {
        return ChatParticipantRes.builder()
                .id(chatParticipant.getId())
                .userId(chatParticipant.getUser().getId())
                .roomId(chatParticipant.getChatRoom().getId())
                .build();
    }
}

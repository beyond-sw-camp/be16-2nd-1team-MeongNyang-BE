package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomSummaryRes {
    private Long id;
    private String roomName;

    public static ChatRoomSummaryRes fromEntity(ChatRoom chatRoom) {
        return ChatRoomSummaryRes.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getName())
                .build();
    }
}

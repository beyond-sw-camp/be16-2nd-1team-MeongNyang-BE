package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMessage;
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
    private String lastMessage;
    private Integer newMessageCount;

    public static ChatRoomSummaryRes fromEntity(ChatRoom chatRoom, int newMessageCount) {
        String lastMessage = "메세지를 보내 채팅을 시작해보세요!";
        if (!chatRoom.getChatMessageList().isEmpty())
            lastMessage = chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size()-1).getContent();

        return ChatRoomSummaryRes.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getName())
                .lastMessage(lastMessage)
                .newMessageCount(newMessageCount)
                .build();
    }
}

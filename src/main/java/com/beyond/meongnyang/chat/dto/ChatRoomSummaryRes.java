package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.common.domain.Bool;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomSummaryRes {
    private Long id;
    private String roomName;
    private String lastMessage;
    private String lastMessageTime;
    private Integer newMessageCount;
    private Long marketPostId;
    private Boolean isPurchaseApproved;

    public static ChatRoomSummaryRes fromEntity(ChatRoom chatRoom, int newMessageCount) {
        String lastMessage = "메세지를 보내 채팅을 시작해보세요!";
        String lastMessageTime = chatRoom.getCreatedAt().toString();

        if (!chatRoom.getChatMessageList().isEmpty()) {
            lastMessage = chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size() - 1).getContent();
            lastMessageTime = chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size() - 1).getCreatedAt().toString();
        }

        return ChatRoomSummaryRes.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getName())
                .lastMessage(lastMessage)
                .newMessageCount(newMessageCount)
                .lastMessageTime(lastMessageTime)
                .isPurchaseApproved(chatRoom.getIsPurchaseApproved() == Bool.TRUE)
                .marketPostId(chatRoom.getMarketPost() == null ? null : chatRoom.getMarketPost().getId())
                .build();
    }
}

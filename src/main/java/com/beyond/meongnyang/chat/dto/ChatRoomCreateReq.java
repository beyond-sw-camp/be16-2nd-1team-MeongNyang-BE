package com.beyond.meongnyang.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateReq {
    private String roomName;
    private List<String> userEmailList;
    private Long marketPostId;
}

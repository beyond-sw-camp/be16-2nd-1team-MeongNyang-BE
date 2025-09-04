package com.beyond.meongnyang.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateReq {
    private String roomName;
    private List<String> userEmailList; // TODO : 프론트에서 전달해 줄 수 있는 정보로 변경 필요
    private Long marketPostId;
}

package com.beyond.meongnyang.chat.dto;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatOnlineParticipantReq {
    private String email;
}

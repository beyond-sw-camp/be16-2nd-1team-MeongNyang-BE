package com.beyond.meongnyang.chat.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipantAddReq {
    private String inviteeEmail;
}

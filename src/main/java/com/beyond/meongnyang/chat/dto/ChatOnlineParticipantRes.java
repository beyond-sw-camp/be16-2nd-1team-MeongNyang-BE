package com.beyond.meongnyang.chat.dto;


import com.beyond.meongnyang.chat.entity.ChatParticipant;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatOnlineParticipantRes {
    private String email;
}

package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.chat.entity.ChatMessage;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageReq {
    private String message;
    private String senderEmail;
    private List<String> fileUrls;
}

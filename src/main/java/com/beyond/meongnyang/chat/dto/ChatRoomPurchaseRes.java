package com.beyond.meongnyang.chat.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomPurchaseRes {
    private Long roomId;
    private Boolean isPurchaseApproved;
}

package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.market.entity.SaleStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomSaleStatusRes {
    private Long roomId;
    private SaleStatus saleStatus;
}

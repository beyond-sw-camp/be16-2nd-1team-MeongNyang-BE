package com.beyond.meongnyang.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmReq {
    private String paymentKey;
    private String orderId;
    private int amount;
}

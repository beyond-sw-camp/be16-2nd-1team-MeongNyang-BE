package com.beyond.meongnyang.market.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmRes {
    private String orderId;
    private int amount;
    private String method;
    private LocalDateTime approvedAt;
    private String status;  // DONE, CANCELED
    private String orderName;
}

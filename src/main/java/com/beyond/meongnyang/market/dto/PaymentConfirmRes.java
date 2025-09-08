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
    private Long amount;
    private String method;
    private String approvedAt;
    private String status;  // DONE, CANCELED
    private String orderName;
}

//{
//        "mId": "tgen_docs",
//        "lastTransactionKey": "txrd_a01k4ktqqk99a8bvqza85gf7ddp",
//        "paymentKey": "tgen_20250908134821R9t82",
//        "orderId": "order_1757306896188_18",
//        "orderName": "123",
//        "taxExemptionAmount": 0,
//        "status": "DONE",
//        "requestedAt": "2025-09-08T13:48:21+09:00",
//        "approvedAt": "2025-09-08T13:53:18+09:00",
//        "useEscrow": false,
//        "cultureExpense": false,
//        "card": null,
//        "virtualAccount": null,
//        "transfer": null,
//        "mobilePhone": null,
//        "giftCertificate": null,
//        "cashReceipt": null,
//        "cashReceipts": null,
//        "discount": null,
//        "cancels": null,
//        "secret": "ps_eqRGgYO1r5Ooazalo0p53QnN2Eya",
//        "type": "NORMAL",
//        "easyPay": {
//        "provider": "카카오페이",
//        "amount": 123,
//        "discountAmount": 0
//        },
//        "country": "KR",
//        "failure": null,
//        "isPartialCancelable": true,
//        "receipt": {
//        "url": "https://dashboard-sandbox.tosspayments.com/receipt/redirection?transactionId=tgen_20250908134821R9t82&ref=PX"
//        },
//        "checkout": {
//        "url": "https://api.tosspayments.com/v1/payments/tgen_20250908134821R9t82/checkout"
//        },
//        "currency": "KRW",
//        "totalAmount": 123,
//        "balanceAmount": 123,
//        "suppliedAmount": 112,
//        "vat": 11,
//        "taxFreeAmount": 0,
//        "method": "간편결제",
//        "version": "2022-11-16",
//        "metadata": null
//        }
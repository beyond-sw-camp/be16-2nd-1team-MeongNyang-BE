package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Table(name = "point_transaction")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private User user;

    private int amount;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;  // EARN(판매적립), WITHDRAW(환전)

    // pg사 결제 (포인트환전 용도)
    private String pgPaymentKey;

    private String method;

    private String status;

    private LocalDateTime createdAt;

    public static PointTransaction earn(User user, int amount) {
        return PointTransaction.builder()
                .user(user)
                .amount(amount)
                .type(PointTransactionType.EARN)
                .status("DONE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static PointTransaction withdraw(User user, int amount, String pgPaymentKey, String method, String status) {
        return PointTransaction.builder()
                .user(user)
                .amount(amount)
                .type(PointTransactionType.WITHDRAW)
                .pgPaymentKey(pgPaymentKey)
                .method(method)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

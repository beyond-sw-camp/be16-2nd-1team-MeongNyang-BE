    package com.beyond.meongnyang.market.entity;

    import com.beyond.meongnyang.common.domain.CommonAt;
    import com.beyond.meongnyang.user.entity.User;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "transaction")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public class Transaction extends CommonAt {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "market_post_id", nullable = false)
        private MarketPost marketPost;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "seller_id", nullable = false)
        private User seller;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "buyer_id", nullable = false)
        private User buyer;

        @Column(nullable = false)
        private int price;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TransactionStatus transactionStatus;

        private LocalDateTime completedAt;

    //    pg사 결제 정보
        @Column(length = 100)
        private String paymentKey;  // TossPayments 결제 고유키

        @Column(length = 50)
        private String paymentMethod;

        // 결제 생성
        public static Transaction create(MarketPost post, User buyer, String paymentKey, String method) {
            return Transaction.builder()
                    .transactionStatus(TransactionStatus.COMPLETED)
                    .marketPost(post)
                    .seller(post.getSeller())
                    .buyer(buyer)
                    .price(post.getPrice())
                    .paymentKey(paymentKey)
                    .paymentMethod(method)
                    .completedAt(LocalDateTime.now())
                    .build();
        }
    }

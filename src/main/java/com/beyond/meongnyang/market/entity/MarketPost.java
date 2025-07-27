package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "market_post")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class MarketPost extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long marketPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(length = 100, nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus saleStatus;

    @Column(nullable = false)
    private boolean isResticted = false;

    @Column(length = 255)
    private String thumbnailUrl;
}

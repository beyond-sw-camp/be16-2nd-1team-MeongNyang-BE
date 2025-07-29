package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ProductImage extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_post_id", nullable = false)
    private MarketPost marketPost;

    @Column(length = 255, nullable = false)
    private String imageUrl;

}

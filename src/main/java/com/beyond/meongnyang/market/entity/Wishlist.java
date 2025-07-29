package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Wishlist extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_post_id", nullable = false)
    private MarketPost marketPost;
}

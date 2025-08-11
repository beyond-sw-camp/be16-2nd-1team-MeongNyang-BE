package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
// 유니크제약조건: 한명의 user가 하나의 거래글만 찜할 수 있음
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "market_post_id"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_post_id", nullable = false)
    private MarketPost marketPost;

    @Column(updatable = false)      // 수정 시 이 필드는 건드리지 않게 함
    private LocalDateTime createdAt;

}

package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.MarketPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPostListReq {
    private Long id;                 // 거래글 id
    private String title;
    private int price;
    private String saleStatus;       // 판매상태 (enum -> String)
    private String thumbnailUrl;
    private long likeCount;           // 찜 개수

    public static MarketPostListReq fromEntity(MarketPost post, long likeCount) {
        return MarketPostListReq.builder()
                .id(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .saleStatus(post.getSaleStatus().name()) // enum → "SALE"
                .thumbnailUrl(post.getThumbnailUrl())
                .likeCount(likeCount)
                .build();
    }
}
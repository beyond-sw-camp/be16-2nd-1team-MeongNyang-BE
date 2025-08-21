package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.Category;
import com.beyond.meongnyang.market.entity.MarketPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPostListRes {
    private Long id;                 // 거래글 id
    private String title;
    private int price;
    private String saleStatus;       // 판매상태 (enum -> String)
    private String thumbnailUrl;
    private int likeCount;           // 찜 개수
    private Category category;
    private String region;

    public static MarketPostListRes fromEntity(MarketPost post, int likeCount) {
        return MarketPostListRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .price(post.getPrice())
                .saleStatus(post.getSaleStatus().name()) // enum → "SALE"
                .thumbnailUrl(post.getThumbnailUrl())
                .likeCount(likeCount)
                .category(post.getCategory())
                .region(post.getRegion())
                .build();
    }
}
package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.Category;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.SaleStatus;
import com.beyond.meongnyang.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostCreateRequest {
    private Integer mainImageIndex;
    private String title;
    private Long categoryId;
    private Integer price;
    private String region;
    private String description;

    public MarketPost toEntity(User seller, Category category, String thumbnailUrl) {
        return MarketPost.builder()
                .title(this.title)
                .description(this.description)
                .price(this.price)
                .region(this.region)
                .category(category)
                .seller(seller)
                .saleStatus(SaleStatus.SALE)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}


package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.Category;
import com.beyond.meongnyang.market.entity.MarketPost;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostCreateReq {
    private Integer mainImageIndex; // 사용자가 선택한 이미지 인덱스
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Category category;
    @NotNull(message = "가격을 입력해주세요.")
    private Integer price;
    @NotBlank(message = "지역을 입력해주세요.")
    private String region;
    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    public MarketPost toEntity() {
        return MarketPost.builder()
                .title(this.title)
                .category(this.category)
                .description(this.description)
                .price(this.price)
                .region(this.region)
                .build();
    }
}


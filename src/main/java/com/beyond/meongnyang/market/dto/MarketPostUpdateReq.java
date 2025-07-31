package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.Category;
import com.beyond.meongnyang.market.entity.MarketPost;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostUpdateReq {
    private Integer mainImageIndex;
    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;
    private Category category;
    private Integer price;
    private String region;
    @NotEmpty(message = "내용을 입력해주세요.")
    private String description;
}

package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPostUpdateReq {
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

}

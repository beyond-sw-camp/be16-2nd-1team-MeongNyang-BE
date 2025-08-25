package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.ProductImage;
import com.beyond.meongnyang.market.entity.SaleStatus;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPostDetailRes {
    private Long id;                         // 거래글 id
    private String title;
//    private String region;
//    private String regionSido;
//    private String regionSigungu;
//    private String regionDong;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private int price;
    private String category;
    private SaleStatus saleStatus;
    private String description;
    private String sellerNickname;           // 판매자 닉네임
    private List<String> productImageList;   // 상품 이미지 URL 리스트
    private String thumbnailUrl;             // 대표 썸네일 이미지 URL
    private boolean isLiked;
    private String sellerProfileUrl;
    private String sellerEmail;

    public static MarketPostDetailRes fromEntity(MarketPost post, boolean isLiked) {
//        List<ProductImage>에서 각 요소(ProductImage 인스턴스)에 대해 getImageUrl() 메서드를 호출
        List<String> urls = post.getProductImageList()
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();

        String profileUrl = "";
        Optional<Pet> petOptional = post.getSeller().getPets().stream().filter(pet -> pet.getId().equals(post.getSeller().getMainPetId())).findFirst();

        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            profileUrl = pet.getPetProfileUrl();
        }

        return MarketPostDetailRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .productImageList(urls)                          // ProductImageList객체에서 가져온 이미지list
                .thumbnailUrl(post.getThumbnailUrl())
//                .region(post.getRegion())
//                .regionSido(post.getRegionSido())
//                .regionSigungu(post.getRegionSigungu())
//                .regionDong(post.getRegionDong())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .createdAt(post.getCreatedAt())
                .price(post.getPrice())
                .category(post.getCategory().name())             // category는 enum타입으로 문자열변환
                .saleStatus(post.getSaleStatus())
                .description(post.getDescription())
                .sellerNickname(post.getSeller().getNickname())  // 판매자의 nickName가져오기
                .isLiked(isLiked)
                .sellerProfileUrl(profileUrl)
                .sellerEmail(post.getSeller().getEmail())
                .build();
    }
}

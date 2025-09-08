    package com.beyond.meongnyang.market.entity;

    import com.beyond.meongnyang.market.dto.MarketPostUpdateReq;
    import com.beyond.meongnyang.user.entity.User;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "market_post")
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    public class MarketPost {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "seller_id", nullable = false)
        private User seller;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "buyer_id", nullable = true)
        private User buyer;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Category category;

        @Column(length = 255, nullable = false)
        private String title;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String description;

        @Column(nullable = false)
        private int price;

        @Column(name = "latitude")
        private Double latitude;

        @Column(name = "longitude")
        private Double longitude;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @Builder.Default
        private SaleStatus saleStatus = SaleStatus.SALE;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        // 관리자 기능 : 게시글 접근 제한 여부
        @Column(nullable = false)
        @Builder.Default
        private boolean isRestricted = false;

        @Column(length = 255)
        private String thumbnailUrl;

        @OneToMany(mappedBy = "marketPost", cascade = CascadeType.ALL, orphanRemoval = true)
        @Builder.Default
        private List<ProductImage> productImageList = new ArrayList<>();

        @Builder.Default
        private String delYn = "N";

        public void setSeller(User user) {
            this.seller = user;
        }
        public void markSold(User buyer) {
            this.buyer = buyer;
            this.saleStatus = SaleStatus.SOLD;
        }
        public void setThumbnailImage(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }
        public void addProductImage(ProductImage productImage) {
            this.productImageList.add(productImage);
        }
        public void updateMarketPost(MarketPostUpdateReq marketPostUpdateReq) {
            this.title = marketPostUpdateReq.getTitle();
            this.category = marketPostUpdateReq.getCategory();
            this.price = marketPostUpdateReq.getPrice();
            this.latitude = marketPostUpdateReq.getLatitude();
            this.longitude = marketPostUpdateReq.getLongitude();
            this.description = marketPostUpdateReq.getDescription();
        }
        public void deleteMarketPost(String delYn){
            this.delYn = delYn;
        }
    }

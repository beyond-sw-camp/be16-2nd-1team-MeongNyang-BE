package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.MarketPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPostRepository extends JpaRepository<MarketPost, Long> {
    // 구매목록 조회용
    Page<MarketPost> findAllByBuyer_Id(Long buyerId, Pageable pageable);

    // 판매목록 조회용
    Page<MarketPost> findAllBySeller_Id(Long sellerId, Pageable pageable);
}

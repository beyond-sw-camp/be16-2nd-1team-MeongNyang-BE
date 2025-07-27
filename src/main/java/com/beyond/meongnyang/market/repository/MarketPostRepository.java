package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.MarketPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPostRepository extends JpaRepository<MarketPost, Long> {
}

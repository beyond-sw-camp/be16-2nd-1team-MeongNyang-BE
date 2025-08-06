package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.Wishlist;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
//    찜 여부 확인
    Optional<Wishlist> findByUserAndMarketPost(User user, MarketPost marketPost);
//    찜 취소
    void deleteByUserAndMarketPost(User user, MarketPost marketPost);
//    찜 목록 조회
    List<Wishlist> findAllByUser(User user);
}

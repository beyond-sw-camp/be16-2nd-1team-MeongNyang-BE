package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.Wishlist;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
//    찜 개수 조회(거래글 목록 조회 시)
    Integer countByMarketPost(MarketPost marketPost);
//    찜 여부 확인
//    Optional<Wishlist> findByUserAndMarketPost(User user, MarketPost marketPost);       // 엔티티 전부 -> 삭제 예정
    boolean existsByUserAndMarketPost(User user, MarketPost marketPost);                // 찜여부만
//    찜 취소
    void deleteByUserAndMarketPost(User user, MarketPost marketPost);
//    찜 목록 조회
    Page<Wishlist> findAllByUser(User user, Pageable pageable);
//    게시글 삭제 시 관련된 찜 삭제 처리
    void deleteAllByMarketPost(MarketPost marketPost);

}

package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>{
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Integer countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long id);

    Page<Like> findAllByPostId(Long postId, Pageable pageable);
}

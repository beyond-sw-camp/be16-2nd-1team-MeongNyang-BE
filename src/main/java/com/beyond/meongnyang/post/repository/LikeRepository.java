package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long>{
    boolean existsByUserIdAndPostId(Long userId, Long postId);


    Integer countByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long id);
}

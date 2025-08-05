package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByPostIdAndUserId(Long postId, Long userId);
}

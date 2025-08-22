package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
        select c
        from Comment c
        where c.post.id = :postId
          and c.delYn = 'FALSE'
          and not exists (
              select 1
              from CommentTag ct
              where ct.comment = c
                and ct.parentComment is not null
          )
        order by c.id desc
    """)
    Page<Comment> findAllByPostIdExcludingReplies(@Param("postId") Long postId, Pageable pageable);

    @Query("""
        select c
        from Comment c
        where c.post.id = :postId
          and c.delYn = 'FALSE'
        order by c.id desc
    """)
    Page<Comment> findAllByPostId(@Param("postId") Long postId, Pageable pageable);
}


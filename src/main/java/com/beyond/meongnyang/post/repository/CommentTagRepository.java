package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.Comment;
import com.beyond.meongnyang.post.entity.CommentTag;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {
    @Query("""
        select distinct ct
        from CommentTag ct
        join fetch ct.comment c
        left join fetch ct.commentUser cu
        left join fetch ct.replyUser ru
        where ct.parentComment.id = :#{#parent.id}
          and c.delYn = 'FALSE'
    """)
    List<CommentTag> findAllByParentComment(@Param("parent") Comment parent);
}

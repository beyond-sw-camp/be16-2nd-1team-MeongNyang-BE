package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.HashTag;
import com.beyond.meongnyang.post.entity.HashTagId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, HashTagId> {

    @Query("""
            select h.tag.id, count(distinct h.post.id)
            from HashTag h
            where h.post.delYn = 'N'
            group by h.tag.id
            order by count(distinct h.post.id) desc,
                     lower(h.tag.name) asc,
                     h.tag.id asc
            """)
    List<Object[]> findTopTagCounts(Pageable pageable);
}

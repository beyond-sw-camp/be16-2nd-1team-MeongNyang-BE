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
    @Query("SELECT t.name, COUNT(h) AS tagCount " +
            "FROM HashTag h JOIN h.tag t " +
            "GROUP BY t.name " +
            "ORDER BY tagCount DESC")
    List<Object[]> findTop5TagNamesWithCount(Pageable pageable);
}

package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.dto.HashtagRankRes;
import com.beyond.meongnyang.post.entity.HashTag;
import com.beyond.meongnyang.post.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}

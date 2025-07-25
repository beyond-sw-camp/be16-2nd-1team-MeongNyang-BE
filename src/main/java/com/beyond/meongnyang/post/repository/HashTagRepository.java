package com.beyond.meongnyang.post.repository;

import com.beyond.meongnyang.post.entity.HashTag;
import com.beyond.meongnyang.post.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
}

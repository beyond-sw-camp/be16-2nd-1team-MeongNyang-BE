package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Long findByFollowerIdAndFollowId(Long following, Long follower);

    Page<Follow> findAll(Specification<Follow> spec, Pageable pageable);

    Optional<Object> findIdByFollowerIdAndFollowId(Long id, Long followingId);
}

package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.UserFollow;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<UserFollow, Long> {
    Long findByFollowerIdAndFollowId(Long following, Long follower);

    Page<UserFollow> findAll(Specification<UserFollow> spec, Pageable pageable);

    Optional<Object> findIdByFollowerAndFollowing(User Follower, User following);
}

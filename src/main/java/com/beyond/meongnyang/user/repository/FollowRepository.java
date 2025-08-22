package com.beyond.meongnyang.user.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.beyond.meongnyang.user.entity.UserFollow;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<UserFollow, Long> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Page<UserFollow> findByFollower(User user, Pageable pageable);

    Page<UserFollow> findByFollowing(User user, Pageable pageable);

    Optional<UserFollow> findIdByFollowerAndFollowing(User follower, User following);
}

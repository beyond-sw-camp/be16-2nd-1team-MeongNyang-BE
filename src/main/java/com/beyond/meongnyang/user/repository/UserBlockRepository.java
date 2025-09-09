package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.UserBlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Long findIdByUserIdAndBlockUserId(Long userId, Long blockUserId);

    Page<UserBlock> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.name LIKE CONCAT('%', :name, '%')")
    Page<UserBlock> findByName(@Param("userId") Long userId, @Param("name") String name, Pageable pageable);
}

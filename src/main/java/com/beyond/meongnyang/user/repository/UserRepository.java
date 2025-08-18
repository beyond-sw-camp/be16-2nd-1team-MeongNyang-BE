package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    Optional<User> findByPhone(String phone);

    Optional<User> findByNickname(String nickname);

//    Optional<User> findByNameAndPhone (String name, String phone);

    Optional<User> findByNameAndEmail (String name, String phone);

    List<User> findAllBydelYn(String delYn);

    Optional<User> findByEmailAndDelYn(String email, String delYn);

    Optional<User> findByIdAndDelYn(Long userId, String delYn);

    // 회원가입 통계
    // 일간 집계 (DATE()로 날짜만 잘라 그룹핑)
    @Query(value = """
        SELECT DATE(u.created_at) AS bucket, COUNT(*) AS cnt
        FROM user u
        WHERE u.created_at BETWEEN :start AND :end
        GROUP BY DATE(u.created_at)
        ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> statDaily(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

    // 주간 집계 (YEARWEEK(...,1) → 주 시작 월요일)
    @Query(value = """
        SELECT STR_TO_DATE(DATE_FORMAT(u.created_at, '%x%v Monday'), '%x%v %W') AS bucket, COUNT(*) AS cnt
        FROM user u
        WHERE u.created_at BETWEEN :start AND :end
        GROUP BY YEARWEEK(u.created_at, 1)
        ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> statWeekly(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    // 월간 집계 (각 월의 1일로 버킷팅)
    @Query(value = """
        SELECT DATE_FORMAT(u.created_at, '%Y-%m-01') AS bucket, COUNT(*) AS cnt
        FROM user u
        WHERE u.created_at BETWEEN :start AND :end
        GROUP BY DATE_FORMAT(u.created_at, '%Y-%m')
        ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> statMonthly(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);
}

package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.admin.dto.UserStatisticsRow;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<User> findByNameAndEmail (String name, String Email);

    List<User> findAllByDelYn(String delYn);

    Optional<User> findByEmailAndDelYn(String email, String delYn);

    Optional<User> findByIdAndDelYn(Long userId, String delYn);

    Optional<User> findBySocialId(String socialId);
  
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.role = :role " +
            "AND u.blockExpiryDate <= :now")
    List<User> findAllExpired(@Param("role") Role role,
                              @Param("now") LocalDateTime now);

    // 회원가입 통계
    // 일간
    @Query(value = """
        SELECT DATE(u.created_at) AS periodStart,
               COUNT(*)           AS signupCount
        FROM `user` u
        WHERE u.created_at >= :start
          AND u.created_at <  :endExclusive
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<UserStatisticsRow> statDaily(@Param("start") LocalDateTime start,
                                      @Param("endExclusive") LocalDateTime endExclusive);

    // 주간(월요일 시작)
    @Query(value = """
        SELECT DATE_SUB(DATE(u.created_at), INTERVAL WEEKDAY(u.created_at) DAY) AS periodStart,
               COUNT(*)                                                         AS signupCount
        FROM `user` u
        WHERE u.created_at >= :start
          AND u.created_at <  :endExclusive
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<UserStatisticsRow> statWeekly(@Param("start") LocalDateTime start,
                                       @Param("endExclusive") LocalDateTime endExclusive);

    // 월간(해당 월 1일)
    @Query(value = """
        SELECT DATE(DATE_FORMAT(u.created_at, '%Y-%m-01')) AS periodStart,
               COUNT(*)                                    AS signupCount
        FROM `user` u
        WHERE u.created_at >= :start
          AND u.created_at <  :endExclusive
        GROUP BY periodStart
        ORDER BY periodStart
        """, nativeQuery = true)
    List<UserStatisticsRow> statMonthly(@Param("start") LocalDateTime start,
                                        @Param("endExclusive") LocalDateTime endExclusive);
}

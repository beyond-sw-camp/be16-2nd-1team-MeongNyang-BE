package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.admin.dto.UserStatisticsRes;
import com.beyond.meongnyang.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

//    // 회원가입 통계
//    // 일간
//    @Query(value = """
//        SELECT DATE(u.created_at) AS date,
//               COUNT(*) AS signupCount
//        FROM user u
//        WHERE DATE(u.created_at) BETWEEN :from AND :to
//        GROUP BY DATE(u.created_at)
//        ORDER BY DATE(u.created_at)
//        """, nativeQuery = true)
//    List<UserStatisticsRes> countSignupsByDay(@Param("from") LocalDate from,
//                                              @Param("to") LocalDate to);
//
//    // 주간
//    @Query(value = """
//        SELECT STR_TO_DATE(CONCAT(YEARWEEK(u.created_at, 1), ' Monday'), '%X%V %W') AS date,
//               COUNT(*) AS signupCount
//        FROM user u
//        WHERE DATE(u.created_at) BETWEEN :from AND :to
//        GROUP BY YEARWEEK(u.created_at, 1)
//        ORDER BY YEARWEEK(u.created_at, 1)
//        """, nativeQuery = true)
//    List<UserStatisticsRes> countSignupsByWeek(@Param("from") LocalDate from,
//                                               @Param("to") LocalDate to);
//
//    // 월간
//    @Query(value = """
//        SELECT DATE_FORMAT(u.created_at, '%Y-%m-01') AS date,
//               COUNT(*) AS signupCount
//        FROM user u
//        WHERE DATE(u.created_at) BETWEEN :from AND :to
//        GROUP BY YEAR(u.created_at), MONTH(u.created_at)
//        ORDER BY YEAR(u.created_at), MONTH(u.created_at)
//        """, nativeQuery = true)
//    List<UserStatisticsRes> countSignupsByMonth(@Param("from") LocalDate from,
//                                                @Param("to") LocalDate to);
}

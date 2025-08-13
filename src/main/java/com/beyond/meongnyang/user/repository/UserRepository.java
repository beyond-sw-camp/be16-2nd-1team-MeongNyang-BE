package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.role = :role " +
            "AND u.blockExpiryDate <= :now")
    List<User> findAllExpired(@Param("role") Role role,
                              @Param("now") LocalDateTime now);
}

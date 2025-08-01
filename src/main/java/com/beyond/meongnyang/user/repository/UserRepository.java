package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByNickname(String nickname);

    List<User> findAllBydelYn(String delYn);

    Optional<User> findByEmailAndDelYn(String email, String delYn);

    Optional<User> findByIdAndDelYn(Long userId, String delYn);
}

package com.beyond.meongnyang.user.repository;

import com.beyond.meongnyang.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByName(String name);

    Optional<User> findBydelYn(String delYn);
}

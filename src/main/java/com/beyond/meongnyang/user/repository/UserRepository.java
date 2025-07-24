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
    // TODO: 이름과 전화번호로 이메일 찾기
    Optional<User> findByNameAndPhone (String name, String phone);
    // TODO: 이메일과 전화번호로 비밀번호 찾기
    Optional<User> findByEmailAndPhone (String email, String phone);
}

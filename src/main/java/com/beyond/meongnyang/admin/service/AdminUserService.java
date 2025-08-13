package com.beyond.meongnyang.admin.service;

import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {
    private final UserRepository userRepository;

    //회원가입 승인
    public Long approveUser(Long id) {
        // 유저 id값을 받아와서
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
        // 해당 유저 회원가입 승인
        user.updateRole(Role.USER);
        // 승인여부 db 저장
        userRepository.save(user);
        return user.getId();
    }
}

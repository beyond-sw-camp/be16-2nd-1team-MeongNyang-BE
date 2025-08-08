package com.beyond.meongnyang.common;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserBlock;
import com.beyond.meongnyang.user.repository.UserBlockRepository;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonService {
    private final UserRepository userRepository;

//    사용자 정보 가져오기
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));
    }
}

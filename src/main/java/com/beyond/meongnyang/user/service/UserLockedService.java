package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserLockedService {

    private final UserRepository userRepository;

    // 실패 횟수 증가 + 잠금 처리
    // 메인 트랜잭션과 분리된, 무조건 커밋되는 작업
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int increaseFailedCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        int newCount = user.getFailedCount() + 1;
        user.updateCount(newCount);

        if (newCount >= 5) {
            user.lockedAccount(); // 잠금 처리
        }
        return newCount;
    }

    // 로그인 성공 시 실패 횟수 초기화
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetFailedCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
        user.updateCount(0);
    }
}
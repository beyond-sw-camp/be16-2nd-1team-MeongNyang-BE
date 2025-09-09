package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    // 임시 비밀번호 발급
    public String generateTempPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // 최소 1개씩 보장
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(special.charAt(random.nextInt(special.length())));

        // 나머지 자리 채우기
        for (int i = 4; i < 10; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }

        // 순서 섞기
        List<Character> charsList = sb.chars().mapToObj(c -> (char)c).collect(Collectors.toList());
        Collections.shuffle(charsList, random);
        StringBuilder finalPassword = new StringBuilder();
        charsList.forEach(finalPassword::append);

        return finalPassword.toString();
    }
}
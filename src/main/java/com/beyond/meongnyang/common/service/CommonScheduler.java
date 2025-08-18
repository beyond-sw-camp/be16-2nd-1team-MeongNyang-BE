package com.beyond.meongnyang.common.service;

import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
@EnableScheduling
public class CommonScheduler {
    private final UserRepository userRepository;
    private final SseService sseService;
    // 1분 주기로 기간 차단 만료된 유저 서비스 이용 차단 자동 해제
    @Scheduled(initialDelay = 10_000, fixedDelay = 60_000) // 애플리케이션 시작 10초 후 첫 실행, 이후 60초마다 실행
    public void isUserBanExpiredCheck() {
        log.info("차단 만료된 유저 검색 시작..");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 만료된 TEMPORARY_BLOCK 유저 조회
        List<User> users = userRepository.findAllExpired(Role.TEMPORARY_BLOCK, now);

        int sent = 0;
        for (User user : users) {
            // 1) 상태 전환
            user.unblock(); // 예: this.role = Role.USER; this.banExpiredAt = null;

            // 2) SSE 발송
            try {
                sseService.publishMessage(
                        "auto-unban",       // topic
                        user.getEmail(),    // receiver
                        "시스템 자동 차단 해제", // sender
                        "차단이 해제되었습니다."
                );
                sent++;
            } catch (Exception e) {
                log.error("SSE 전송 실패: userEmail={}", user.getEmail(), e);
            }
        }

        log.info("자동 해제 {}명 처리, SSE 통지 {}건 완료", users.size(), sent);
    }
}

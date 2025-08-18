package com.beyond.meongnyang.admin.service;

import com.beyond.meongnyang.admin.dto.AdminUserUpdateReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsRes;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {
    private final UserRepository userRepository;

    // 회원가입 승인
    public Long approveUser(Long id) {
            // 유저 id값을 받아와서
            User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
            // 해당 유저 회원가입 승인
            user.approve();
            // 승인여부 db 저장
            userRepository.save(user);
        return user.getId();
    }

    // 회원정보 수정
    public Long updateUser(Long id, AdminUserUpdateReq req) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
        //엔티티 수정
        user.updateUser(req);
        return user.getId();
    }

    // 회원정보 삭제
    public Long deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("없는 회원입니다."));
        //delYn = Y로 변경
        user.softDelete();
        return user.getId();
    }

    // 회원가입 통계
    public List<UserStatisticsRes> findUserSignupStatistics(UserStatisticsReq req) {
        // 1. 요청 파라미터 보정
        LocalDate to = Optional.ofNullable(req.getTo()).orElse(LocalDate.now());
        LocalDate from = Optional.ofNullable(req.getFrom()).orElse(to.minusDays(29));
        if (from.isAfter(to)) throw new IllegalArgumentException("from은 to보다 이후일 수 없습니다.");

        String grain = normalize(req.getGrain());
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        // 2. DB 집계 결과 가져오기
        List<Object[]> rows = switch (grain) {
            case "일간" -> userRepository.statDaily(start, end);
            case "주간" -> userRepository.statWeekly(start, end);
            case "월간" -> userRepository.statMonthly(start, end);
            default -> throw new IllegalArgumentException("grain은 일간/주간/월간 중 하나여야 합니다.");
        };

        // 3. DB 결과 -> Map 변환
        Map<LocalDate, Long> actual = new HashMap<>();
        for (Object[] r : rows) {
            LocalDate bucket = toLocalDateSafe(r[0]);       // 날짜 변환
            long cnt = ((Number) r[1]).longValue();         // COUNT 값
            actual.put(bucket, cnt);
        }

        // 4. 전체 버킷(날짜 리스트) 생성 → 빈 구간 0 채우기
        List<LocalDate> buckets = switch (grain) {
            case "일간" -> datesBetween(from, to);
            case "주간" -> weeksBetween(toMonday(from), toMonday(to));
            case "월간" -> monthsBetween(firstOfMonth(from), firstOfMonth(to));
            default -> throw new IllegalArgumentException();
        };

        // 5. 최종 응답 DTO로 변환
        return buckets.stream()
                .map(b -> new UserStatisticsRes(b, actual.getOrDefault(b, 0L)))
                .collect(Collectors.toList());
    }

    // grain 값 보정 (한글/영문 모두 지원)
    private String normalize(String g) {
        if (g == null) return "일간";
        String s = g.trim().toLowerCase();
        return switch (s) {
            case "일간", "daily" -> "일간";
            case "주간", "weekly" -> "주간";
            case "월간", "monthly" -> "월간";
            default -> throw new IllegalArgumentException("grain=일간/주간/월간(daily/weekly/monthly)");
        };
    }

    // DB 결과(Object) → LocalDate 변환
    private LocalDate toLocalDateSafe(Object bucketCol) {
        if (bucketCol instanceof java.sql.Date d) return d.toLocalDate();
        String s = bucketCol.toString(); // "2025-08-01 ..." 같은 형식 가정
        return LocalDate.parse(s.substring(0, 10));
    }

    // 월요일로 보정
    private static LocalDate toMonday(LocalDate d) {
        return d.minusDays((d.getDayOfWeek().getValue() + 6) % 7);
    }

    // 그 달의 1일로 보정
    private static LocalDate firstOfMonth(LocalDate d) {
        return d.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
    }

    // from~to 모든 날짜 리스트
    private static List<LocalDate> datesBetween(LocalDate from, LocalDate to) {
        long days = to.toEpochDay() - from.toEpochDay() + 1;
        return java.util.stream.Stream.iterate(from, d -> d.plusDays(1)).limit(days).toList();
    }

    // from~to 모든 주 시작일(월요일) 리스트
    private static List<LocalDate> weeksBetween(LocalDate startMonday, LocalDate endMonday) {
        long weeks = (endMonday.toEpochDay() - startMonday.toEpochDay()) / 7 + 1;
        return java.util.stream.Stream.iterate(startMonday, d -> d.plusWeeks(1)).limit(weeks).toList();
    }

    // from~to 모든 월의 1일 리스트
    private static List<LocalDate> monthsBetween(LocalDate startMonth, LocalDate endMonth) {
        int months = (endMonth.getYear() - startMonth.getYear()) * 12
                + endMonth.getMonthValue() - startMonth.getMonthValue() + 1;
        return java.util.stream.Stream.iterate(startMonth, d -> d.plusMonths(1)).limit(months).toList();
    }

}

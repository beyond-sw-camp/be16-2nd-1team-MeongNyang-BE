package com.beyond.meongnyang.admin.service;

import com.beyond.meongnyang.admin.dto.AdminUserUpdateReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsReq;
import com.beyond.meongnyang.admin.dto.UserStatisticsRes;
import com.beyond.meongnyang.admin.dto.UserStatisticsRow;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        // 1) 파라미터 보정
        LocalDate to   = Optional.ofNullable(req.getTo()).orElse(LocalDate.now());
        LocalDate from = Optional.ofNullable(req.getFrom()).orElse(to.minusDays(29));
        if (from.isAfter(to)) throw new IllegalArgumentException("from은 to보다 이후일 수 없습니다.");

        // 2) 집계 단위 정규화 (한/영 지원)
        String grain = normalize(req.getGrain()); // "일간"/"주간"/"월간"

        // 3) 경계: [start, endExclusive)
        LocalDateTime start        = from.atStartOfDay();
        LocalDateTime endExclusive = to.plusDays(1).atStartOfDay();

        // 4) DB 집계 호출
        List<UserStatisticsRow> rows = switch (grain) {
            case "일간" -> userRepository.statDaily(start, endExclusive);
            case "주간" -> userRepository.statWeekly(start, endExclusive);
            case "월간" -> userRepository.statMonthly(start, endExclusive);
            default -> throw new IllegalArgumentException("grain은 일간/주간/월간(daily/weekly/monthly) 중 하나여야 합니다.");
        };

        // 5) 결과 맵 (SQL Date → LocalDate)
        Map<LocalDate, Long> actual = rows.stream()
                .collect(Collectors.toMap(
                        r -> r.getPeriodStart(),
                        r -> Optional.ofNullable(r.getSignupCount()).orElse(0L)
                ));

        // 6) 버킷 채우기(빈 구간 0)
        List<LocalDate> buckets = switch (grain) {
            case "일간" -> datesBetween(from, to);
            case "주간" -> weeksBetween(toMonday(from), toMonday(to));            // from/to를 각각 그 주의 월요일로 보정한 뒤, 모든 월요일
            case "월간" -> monthsBetween(firstOfMonth(from), firstOfMonth(to));   // from/to를 각각 그 달의 1일로 보정한 뒤, 모든 “1일”
            default -> throw new IllegalArgumentException();
        };

        // 7) 버킷 순서대로 DTO(UserStatisticsRes)로 변환 (없으면 0L로 채워 반환)
        return buckets.stream()
                .map(b -> new UserStatisticsRes(b, actual.getOrDefault(b, 0L)))
                .toList();
    }

    // ========= 유틸 =========
    // "daily/weekly/monthly" 또는 "일간/주간/월간"을 받아 "일간/주간/월간"으로 통일
    private String normalize(String g) {
        if (g == null) return "일간";         // 입력이 null이면 "일간"
        String s = g.trim().toLowerCase();
        return switch (s) {
            case "일간", "daily" -> "일간";
            case "주간", "weekly" -> "주간";
            case "월간", "monthly" -> "월간";
            default -> throw new IllegalArgumentException("grain=일간/주간/월간(daily/weekly/monthly)");
        };
    }
//    주간 버킷을 "월요일 시작"으로 맞추기 위한 보정 함수.
    private static LocalDate toMonday(LocalDate d) {
        return d.minusDays((d.getDayOfWeek().getValue() + 6) % 7);
    }
//    월간 버킷을 "그 달의 1일"로 맞추는 보정 함수.
    private static LocalDate firstOfMonth(LocalDate d) {
        return d.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
    }

//    오름차순 정렬
    private static List<LocalDate> datesBetween(LocalDate from, LocalDate to) {
        long days = to.toEpochDay() - from.toEpochDay() + 1;
        return java.util.stream.Stream.iterate(from, d -> d.plusDays(1)).limit(days).toList();
    }
    private static List<LocalDate> weeksBetween(LocalDate startMonday, LocalDate endMonday) {
        long weeks = (endMonday.toEpochDay() - startMonday.toEpochDay()) / 7 + 1;
        return java.util.stream.Stream.iterate(startMonday, d -> d.plusWeeks(1)).limit(weeks).toList();
    }
    private static List<LocalDate> monthsBetween(LocalDate startMonth, LocalDate endMonth) {
        int months = (endMonth.getYear() - startMonth.getYear()) * 12
                + endMonth.getMonthValue() - startMonth.getMonthValue() + 1;
        return java.util.stream.Stream.iterate(startMonth, d -> d.plusMonths(1)).limit(months).toList();
    }

}

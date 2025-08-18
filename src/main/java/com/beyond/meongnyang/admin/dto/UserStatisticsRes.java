    package com.beyond.meongnyang.admin.dto;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class UserStatisticsRes {
        private LocalDate date;     // 일자(또는 주 시작일, 월 시작일)
        private long signupCount;   // 회원가입 수
    }

package com.beyond.meongnyang.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsReq {
    private LocalDate from;
    private LocalDate to;
    private String grain;    // 해당 기간 내 조회기준 (일간/주간/월간)
}

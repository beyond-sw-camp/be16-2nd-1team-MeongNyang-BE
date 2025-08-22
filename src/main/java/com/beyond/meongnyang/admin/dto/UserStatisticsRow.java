package com.beyond.meongnyang.admin.dto;

import java.time.LocalDate;

public interface UserStatisticsRow {
    LocalDate getPeriodStart();
    Long getSignupCount();
}
package com.beyond.meongnyang.admin.entity;

public enum ReportResult {
    TEMPORARY_BLOCK,    // 기간 차단
    PERMANENT_BLOCK,    // 영구 차단
    POST_DELETE,
    DENY    // 신고 거부(기각)
}

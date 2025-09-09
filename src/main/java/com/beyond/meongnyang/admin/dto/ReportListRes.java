package com.beyond.meongnyang.admin.dto;

import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportStatus;
import com.beyond.meongnyang.admin.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportListRes {
    private Long id;
    private String reportUserName;
    private ReportType reportType;
    private String reason;
    private ReportStatus reportStatus;

    public static ReportListRes fromEntity(Report report){
        return ReportListRes.builder()
                .id(report.getId())
                .reportUserName(report.getReporterUser().getNickname())
                .reportType(report.getReportType())
                .reportStatus(report.getReportStatus())
                .reason(report.getReason())
                .build();
    }
}

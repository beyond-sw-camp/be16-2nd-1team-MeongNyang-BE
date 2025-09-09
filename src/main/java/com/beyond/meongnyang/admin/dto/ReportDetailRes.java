package com.beyond.meongnyang.admin.dto;

import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportStatus;
import com.beyond.meongnyang.admin.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportDetailRes {
    private Long id;
    private ReportType reportType;
    private ReportStatus reportStatus;
    private String reportUserName;
    private String reportedUserName;
    private String content;
    private String reason;
    private String date;

    public static ReportDetailRes fromEntity(Report report, String content){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        return ReportDetailRes.builder()
                .id(report.getId())
                .reportType(report.getReportType())
                .reportStatus(report.getReportStatus())
                .reportUserName(report.getReporterUser().getNickname())
                .reportedUserName(report.getReportedUser().getNickname())
                .content(content)
                .reason(report.getReason())
                .date(report.getCreatedAt().format(formatter))
                .build();
    }
}

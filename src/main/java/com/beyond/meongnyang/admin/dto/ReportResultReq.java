package com.beyond.meongnyang.admin.dto;

import com.beyond.meongnyang.admin.entity.ReportResult;
import com.beyond.meongnyang.admin.entity.ReportStatus;
import com.beyond.meongnyang.admin.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportResultReq {
    private String userEmail;
    private ReportResult reportResult;
    private int blockSeconds;
}

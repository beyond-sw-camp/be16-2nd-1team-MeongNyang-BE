package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportStatus;
import com.beyond.meongnyang.admin.entity.ReportType;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostReportCreateReq {
    private String reason;
    public Report ReportToEntity(Post post, User user){
        return Report.builder()
                .post(post)
                .reporterUser(user)
                .reason(this.reason)
                .reportType(ReportType.POST)
                .build();
    }
}

package com.beyond.meongnyang.market.dto;

import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportType;
import com.beyond.meongnyang.market.entity.MarketPost;
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
public class MarketReportCreateReq {
    private String reason;
    public Report ReportToEntity(MarketPost marketPost, User user){
        return Report.builder()
                .marketPost(marketPost)
                .reporterUser(user)
                .reason(this.reason)
                .reportType(ReportType.MARKET)
                .build();
    }
}

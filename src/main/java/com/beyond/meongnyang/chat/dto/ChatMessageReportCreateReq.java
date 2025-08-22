package com.beyond.meongnyang.chat.dto;

import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportType;
import com.beyond.meongnyang.chat.entity.ChatMessage;
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
public class ChatMessageReportCreateReq {
    private String reason;
    public Report ReportToEntity(ChatMessage chatMessage, User user){
        return Report.builder()
                .chatMessage(chatMessage)
                .reporterUser(user)
                .reason(this.reason)
                .reportType(ReportType.POST)
                .build();
    }
}

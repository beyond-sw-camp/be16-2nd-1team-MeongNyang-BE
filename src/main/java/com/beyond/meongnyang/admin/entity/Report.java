package com.beyond.meongnyang.admin.entity;

import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Report extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporterUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_post_id")
    private MarketPost marketPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    @Column(length = 255, nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReportStatus reportStatus = ReportStatus.WAITING;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    public void updateReportStatus(ReportStatus reportStatus){
        this.reportStatus = reportStatus;
    }
}
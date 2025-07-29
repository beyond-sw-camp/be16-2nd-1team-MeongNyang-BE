package com.beyond.meongnyang.market.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reporter_id", nullable = false)
//    private User reporter;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "reported_user_id")
//    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_post_id")
    private MarketPost marketPost;

    private Long postId;
    private Long messageId;

    @Column(length = 255, nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus reportStatus;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;
}
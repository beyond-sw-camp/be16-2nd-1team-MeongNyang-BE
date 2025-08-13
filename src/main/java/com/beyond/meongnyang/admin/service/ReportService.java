package com.beyond.meongnyang.admin.service;

import com.beyond.meongnyang.admin.dto.ReportDetailRes;
import com.beyond.meongnyang.admin.dto.ReportListRes;
import com.beyond.meongnyang.admin.dto.ReportResultReq;
import com.beyond.meongnyang.admin.entity.Report;
import com.beyond.meongnyang.admin.entity.ReportResult;
import com.beyond.meongnyang.admin.entity.ReportStatus;
import com.beyond.meongnyang.admin.entity.ReportType;
import com.beyond.meongnyang.admin.repository.ReportRepository;
import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.chat.repository.ChatMessageRepository;
import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.common.dto.SseMessageRes;
import com.beyond.meongnyang.common.service.SseService;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.repository.MarketPostRepository;
import com.beyond.meongnyang.post.dto.PostListReq;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.post.repository.PostRepository;
import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import com.beyond.meongnyang.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.beyond.meongnyang.user.entity.Role.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MarketPostRepository marketPostRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final EntityManager em;
    private final SseService sseService;
    private final CommonService commonService;
    private final UserService userService;

    // 모든 신고조회
    @Transactional(readOnly = true)
    public Page<ReportListRes> findAll(Pageable pageable) {
        Page<Report> reportList = reportRepository.findAll(pageable);
        return reportList.map(ReportListRes::fromEntity);
    }

    // 신고 상세 조회
    @Transactional(readOnly = true)
    public ReportDetailRes findById(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));
        // 신고 유형별로 content 할당
        String content = "";
        switch (report.getReportType()) {
            case POST -> {
                Post post = postRepository.findById(report.getPost().getId()).orElseThrow(() -> new EntityNotFoundException("일기가 존재하지 않습니다."));
                content = post.getContent();
            }
            case CHAT -> {
                ChatMessage chatMessage = chatMessageRepository.findById(report.getChatMessage().getId()).orElseThrow(() -> new EntityNotFoundException("일기가 존재하지 않습니다."));
                //ToDo: 이 부분은 해당 기능 담당자가 수정해주세요(신고 대상 글 또는 content가 담기게)
            }

            case MARKET -> {
                MarketPost marketPost = marketPostRepository.findById(report.getMarketPost().getId()).orElseThrow(() -> new EntityNotFoundException("일기가 존재하지 않습니다."));
                //ToDo: 이 부분은 해당 기능 담당자가 수정해주세요(신고 대상 글 또는 content가 담기게)
            }
        }

        return ReportDetailRes.fromEntity(report, content);
    }

    // 신고 처리
    public void processReport(Long reportId, ReportResultReq req) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("신고가 존재하지 않습니다."));

        if (req.getReportResult() == ReportResult.DENY) {
            report.updateReportStatus(ReportStatus.DENY);
            return;
        }

        report.updateReportStatus(ReportStatus.COMPLETE);

        User admin = commonService.getCurrentUser();
        User reportedUser = userRepository.findById(report.getReportedUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        switch (req.getReportResult()) {
            case TEMPORARY_BLOCK -> userService.handleBan(admin, reportedUser, TEMPORARY_BLOCK, (long) req.getBlockSeconds());
            case PERMANENT_BLOCK -> userService.handleBan(admin, reportedUser, PERMANENT_BLOCK, 0L);
            case POST_DELETE -> {
                Post post = postRepository.findById(report.getPost().getId())
                        .orElseThrow(() -> new EntityNotFoundException("일기가 없습니다."));
                post.deletePost("Y");
            }
        }
    }
}

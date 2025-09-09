package com.beyond.meongnyang.notification.service;

import com.beyond.meongnyang.common.service.SseService;
import com.beyond.meongnyang.notification.dto.NotificationRes;
import com.beyond.meongnyang.notification.entity.Notification;
import com.beyond.meongnyang.notification.entity.NotificationType;
import com.beyond.meongnyang.notification.repository.NotificationRepository;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CommonService commonService;
    private final SseService sseService;
    private final ObjectMapper objectMapper;

    /**
     * 알림 엔티티를 생성하여 저장한 뒤, 생성된 알림을 DTO로 변환하고 SSE 채널을 통해 수신자에게 즉시 전송합니다.
     *
     * @param targetId 알림이 참조하는 도메인 대상의 식별자입니다(예: 게시글, 댓글, 주문 등).
     * @param receiver 알림을 수신할 사용자 엔티티입니다.
     * @param content 알림 메시지 본문 내용입니다.
     * @param type 알림의 종류를 나타내는 열거형 타입입니다.
     *
     * @throws RuntimeException JSON 직렬화 과정에서 JsonProcessingException이 발생한 경우 런타임 예외로 포장되어 전파됩니다.
     *
     * @implNote 저장은 리포지토리의 save 동작을 통해 수행되며, 메시지 전송은 SSE 기반 서비스에서 수신자 이메일을 키로 사용해 발행됩니다.
     * @see com.beyond.meongnyang.notification.entity.NotificationType type
     */
    public void create(Long targetId, User receiver, String content, NotificationType type) {
        Notification notification = Notification.builder()
                .content(content)
                .receiver(receiver)
                .targetId(targetId)
                .notificationType(type)
                .build();
        notificationRepository.save(notification);
        NotificationRes notificationRes = NotificationRes.fromEntity(notification);
        try {
            sseService.publishMessage("notification", receiver.getEmail(), objectMapper.writeValueAsString(notificationRes));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<NotificationRes> findMyAlarms() {
        return notificationRepository.findAllByReceiver(commonService.getCurrentUser()).stream().map(NotificationRes::fromEntity).toList();
    }

    public void deleteById(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("alarm not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!notification.getReceiver().getEmail().equals(email)) throw new AccessDeniedException("Not your alarm");

        notificationRepository.delete(notification);
    }

    public void deleteMyAlarms() {
        notificationRepository.deleteAllByReceiver(commonService.getCurrentUser());
    }

    public void readById(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("alarm not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!notification.getReceiver().getEmail().equals(email)) throw new AccessDeniedException("Not your alarm");

        notification.read();
    }

    public void readMyAlarms() {
        notificationRepository.findAllByReceiverAndIsRead(commonService.getCurrentUser(), Bool.FALSE).forEach(Notification::read);
    }
}

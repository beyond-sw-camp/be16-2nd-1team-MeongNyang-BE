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

    public void create(Long targetId, User receiver, String content, NotificationType type) {
        Notification notification = Notification.builder()
                .content(content)
                .receiver(receiver)
                .targetId(targetId)
                .notificationType(type)
                .build();
        notificationRepository.save(notification);
        try {
            sseService.publishMessage("notification", receiver.getEmail(), objectMapper.writeValueAsString(notification));
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

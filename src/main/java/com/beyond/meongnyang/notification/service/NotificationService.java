package com.beyond.meongnyang.notification.service;

import com.beyond.meongnyang.notification.dto.NotificationRes;
import com.beyond.meongnyang.notification.entity.Notification;
import com.beyond.meongnyang.notification.repository.NotificationRepository;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.service.CommonService;
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

    public void create(Notification notification) {
        notificationRepository.save(notification);
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

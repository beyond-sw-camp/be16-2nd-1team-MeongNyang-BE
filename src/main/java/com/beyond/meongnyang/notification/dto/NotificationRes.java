package com.beyond.meongnyang.notification.dto;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.notification.entity.Notification;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationRes {
    private Long id;
    private String content;
    private String alarmType;
    private Long targetId;
    private Bool isRead;
    private String createdAt;

    public static NotificationRes fromEntity(Notification notification) {
        return NotificationRes.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .alarmType(notification.getNotificationType().toString())
                .targetId(notification.getTargetId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt().toString())
                .build();
    }
}

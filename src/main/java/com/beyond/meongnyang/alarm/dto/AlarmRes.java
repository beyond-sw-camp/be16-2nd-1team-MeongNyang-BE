package com.beyond.meongnyang.alarm.dto;

import com.beyond.meongnyang.alarm.entity.Alarm;
import com.beyond.meongnyang.common.domain.Bool;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AlarmRes {
    private Long id;
    private String content;
    private String alarmType;
    private Long targetId;
    private Bool isRead;
    private String createdAt;

    public static AlarmRes fromEntity(Alarm alarm) {
        return AlarmRes.builder()
                .id(alarm.getId())
                .content(alarm.getContent())
                .alarmType(alarm.getAlarmType().toString())
                .targetId(alarm.getTargetId())
                .isRead(alarm.getIsRead())
                .createdAt(alarm.getCreatedAt().toString())
                .build();
    }
}

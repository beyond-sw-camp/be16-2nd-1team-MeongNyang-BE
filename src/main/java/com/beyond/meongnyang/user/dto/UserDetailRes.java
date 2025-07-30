package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class UserDetailRes {
    private Long userId;
    private String nickname;
    private String email;
    private String phone;
    private int point;
    private boolean isLocked;
    private LocalDateTime isLockedAt;
    private LocalDateTime passwordChangeAt;
    private String delYn;
    private LocalDateTime deletedAt;

    public static UserDetailRes fromEntity(User user) {
        return UserDetailRes.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .point(user.getPoint())
                .isLocked(user.isLocked())
                .isLockedAt(user.getIsLockedAt())
                .passwordChangeAt(user.getPasswordChangeAt())
                .delYn(user.getDelYn())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}


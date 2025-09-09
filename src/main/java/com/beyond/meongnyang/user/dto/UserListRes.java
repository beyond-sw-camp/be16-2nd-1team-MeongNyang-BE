package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class UserListRes {

    private Long userId;
    private String nickname;
    private String email;
//    private String phone;
    private int point;
    private String isLocked;
    private String delYn;

    public static UserListRes fromEntity(User user) {
        return UserListRes.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
//                .phone(user.getPhone())
                .point(user.getPoint())
                .isLocked(user.getIsLocked())
                .delYn(user.getDelYn())
                .build();
    }
}

package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MyPageRes {
    private Long userId;
    private String nickname;
    private String email;
    private LocalDateTime createdAt;
    private Long mainPetId;
    private String mainPetImage;
    private SocialType socialType;
}

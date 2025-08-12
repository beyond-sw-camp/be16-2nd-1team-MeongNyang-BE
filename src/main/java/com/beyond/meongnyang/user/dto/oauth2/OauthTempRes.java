package com.beyond.meongnyang.user.dto.oauth2;

import com.beyond.meongnyang.user.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OauthTempRes {
    private String socialId;
    private String email;
    private SocialType socialType;
    private String refreshToken;
}
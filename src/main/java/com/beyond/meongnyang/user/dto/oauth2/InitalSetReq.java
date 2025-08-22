package com.beyond.meongnyang.user.dto.oauth2;

import com.beyond.meongnyang.user.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InitalSetReq {
    private String name;
    private String nickname;
    private String socialId;
    private SocialType socialType;
}
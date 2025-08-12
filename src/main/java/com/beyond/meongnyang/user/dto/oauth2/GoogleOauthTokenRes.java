package com.beyond.meongnyang.user.dto.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 없는 필드는 자동으로 ignore
public class GoogleOauthTokenRes {
    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String id_token;
}
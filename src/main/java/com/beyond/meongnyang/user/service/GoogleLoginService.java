package com.beyond.meongnyang.user.service;


import com.beyond.meongnyang.user.dto.oauth2.GoogleOauthTokenRes;
import com.beyond.meongnyang.user.dto.oauth2.GoogleProfileRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class GoogleLoginService {
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;


    public GoogleOauthTokenRes getAccessToken(String code) {
        // 인가코드, clientId, clientSecret, redirectUri, grantType 넘겨받아야 함
        RestClient restClient = RestClient.create();
        // MutilValueMap을 통해 자동으로 form-data로 변환
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<GoogleOauthTokenRes> response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(params)
                .retrieve() // 응답 body값만을 추출
                .toEntity(GoogleOauthTokenRes.class); // response body를 OauthAccessTokenRes로 변환

        return response.getBody();
    }

    public GoogleProfileRes getGoogleProfile(String accessToken) {
        RestClient restClient = RestClient.create();
        ResponseEntity<GoogleProfileRes> response = restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve() // 응답 body값만을 추출
                .toEntity(GoogleProfileRes.class); // response body를 GoogleAccessTokenReq로 변환

        return response.getBody();
    }
}
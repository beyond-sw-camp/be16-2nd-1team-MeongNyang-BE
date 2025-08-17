package com.beyond.meongnyang.user.service;


import com.beyond.meongnyang.user.dto.oauth2.KakaoOauthTokenRes;
import com.beyond.meongnyang.user.dto.oauth2.KakapProfileRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Transactional
public class KakaoLoginService {
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;


    public KakaoOauthTokenRes getAccessToken(String code ) {
        // 인가코드, clientId, clientSecret, redirectUri, grantType 넘겨받음
        RestClient restClient = RestClient.create();
        // MutilValueMap을 통해 자동으로 form-data로 변환
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<KakaoOauthTokenRes> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(params)
                .retrieve() // 응답 body값만을 추출
                .toEntity(KakaoOauthTokenRes.class); // response body를 OauthAccessTokenRes로 변환

        return response.getBody();
    }

    public KakapProfileRes getKakaoProfile(String accessToken) {
        RestClient restClient = RestClient.create();
        ResponseEntity<KakapProfileRes> response = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve() // 응답 body값만을 추출
                .toEntity(KakapProfileRes.class); // response body를 KakaoAccessTokenReq로 변환

        return response.getBody();
    }
}
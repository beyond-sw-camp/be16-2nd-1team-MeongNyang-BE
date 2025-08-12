package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.config.RedisConfig;
import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.common.security.JwtTokenProvider;
import com.beyond.meongnyang.common.service.RedisService;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.dto.oauth2.*;
import com.beyond.meongnyang.user.entity.SocialType;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.service.GoogleLoginService;
import com.beyond.meongnyang.user.service.KakaoLoginService;
import com.beyond.meongnyang.user.service.SendEmailService;
import com.beyond.meongnyang.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserRestController {
    // 임시저장rt
    @Value("${jwt.tempExpirationRt}")
    Long tempExpirationRt;
    // 완전 저장
    @Value("${jwt.expirationRt}")
    Long expirationRt;

    public final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleLoginService googleLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final RedisService redisService;


    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@Valid @RequestBody UserCheckEmailReq dto) {
        this.userService.checkEmail(dto);
        return new ResponseEntity<>(CommonRes.ofSuccess(dto.getEmail(), HttpStatus.OK.value(), "사용가능한 이메일입니다."), HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> sendCode(@RequestBody UserCheckEmailReq req) {
        this.userService.sendCode(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                null, HttpStatus.OK.value(), "인증번호가 발급되었습니다."
        ), HttpStatus.OK);
    }
    @PostMapping("/verify-email-check")
    public ResponseEntity<?> verifyCode(@RequestBody UserEmailVerifyReq req) {
       this.userService.verifyCode(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
            null, HttpStatus.OK.value(), "인증 완료되었습니다."
        ), HttpStatus.OK);
    }
    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@Valid @RequestBody UserCheckNicknameReq dto) {
        this.userService.checkNickname(dto);
        return new ResponseEntity<>(CommonRes.ofSuccess(dto.getNickname(), HttpStatus.OK.value(), "사용가능한 사용자명입니다."), HttpStatus.OK);
    }

//    @PostMapping("/check-phone")
//    public ResponseEntity<?> checkPhone(@Valid @RequestBody UserCheckPhoneReq dto) {
//        this.userService.checkPhone(dto);
//        return new ResponseEntity<>(CommonRes.ofSuccess(dto.getPhone(), HttpStatus.OK.value(), "사용가능한 전화번호입니다."), HttpStatus.OK);
//    }

    // 회원가입
    @PostMapping("/sign")
    public ResponseEntity<?> save(@Valid @RequestBody UserCreateReq dto) {
        this.userService.save(dto);
        return new ResponseEntity<>(CommonRes.ofSuccess(dto, HttpStatus.CREATED.value(), "회원가입이 완료되었습니다."), HttpStatus.CREATED);
    }
    // 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<?> accessLogin(@Valid @RequestBody UserLoginReq request) {
        this.userService.accessLogin(request);
        User user = this.userService.accessLogin(request);
        String atToken = jwtTokenProvider.createAtToken(user);
        String rtToken = jwtTokenProvider.createRtToken(user);

        UserLoginRes token = UserLoginRes.builder()
                .accessToken(atToken)
                .refreshToken(rtToken)
                .build();

        return new ResponseEntity<>(CommonRes.ofSuccess(token, HttpStatus.OK.value(), "로그인되었습니다."), HttpStatus.OK);
    }
    // google Login
    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@Valid @RequestBody RedirectReq redirectReq) {
        // accessToken 발급
        GoogleOauthTokenRes googleOauthTokenRes = this.googleLoginService.getAccessToken(redirectReq.getCode());
        // 사용자 정보 얻기
        GoogleProfileRes googleProfileRes = this.googleLoginService.getGoogleProfile(googleOauthTokenRes.getAccess_token());
        // 회원가입이 되어있지 않다면 회원가입 시키기
        Optional<User> optionalUser = this.userService.getUserBySocailId(googleProfileRes.getSub());
        if(optionalUser.isEmpty()){
            Long ttl = tempExpirationRt * 60L;  // 15분
            OauthTempRes tempData = new OauthTempRes(
                    googleProfileRes.getSub(),
                    googleProfileRes.getEmail(),
                    SocialType.GOOGLE,
                    googleOauthTokenRes.getRefresh_token());

            redisService.saveObject("TMP_RT:GOOGLE:" + googleProfileRes.getSub(), tempData, ttl);

            return new ResponseEntity(CommonRes.ofSuccess(
                    Map.of(
                            "isNewUser", true,
                            "socialId", googleProfileRes.getSub(),
                            "email", googleProfileRes.getEmail(),
                            "socialType", SocialType.GOOGLE), HttpStatus.CREATED.value(), "추가 정보를 입력해주세요"), HttpStatus.CREATED);
        }

        // 회원가입이 되어있는 회원이라면 토큰 발급
        User user = optionalUser.get();
        String atToken = jwtTokenProvider.createAtToken(user);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of(
                        "isNewUser", false,
                        "id", user.getId(),
                        "token", atToken
                ), HttpStatus.OK.value(), "로그인되었습니다."
        ), HttpStatus.OK);
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", user.getId());
//        loginInfo.put("token", atToken);
//        return new ResponseEntity<>(CommonRes.ofSuccess(loginInfo, HttpStatus.OK.value(), "로그인되었습니다."), HttpStatus.OK);


    }

    // kakao Login
    @PostMapping("/kakao/login")
    public ResponseEntity<?> kakaoLogin(@Valid @RequestBody RedirectReq redirectReq) {
        System.out.println("카카오 인가 코드: " + redirectReq.getCode());


        KakaoOauthTokenRes oauthTokenRes = this.kakaoLoginService.getAccessToken(redirectReq.getCode());
        System.out.println("카카오 access_token: " + oauthTokenRes.getAccess_token());

        KakapProfileRes kakaoProfileRes = this.kakaoLoginService.getKakaoProfile(oauthTokenRes.getAccess_token());
        System.out.println("카카오 유저 ID: " + kakaoProfileRes.getId());
        System.out.println("카카오 유저 email: " + kakaoProfileRes.getKakao_account().getEmail());

        Optional<User> optionalUser = this.userService.getUserBySocailId(kakaoProfileRes.getId());
        if (optionalUser.isEmpty()) {
            Long ttl = tempExpirationRt * 60L;  // 15분
            OauthTempRes tempData = new OauthTempRes(
                    kakaoProfileRes.getId(),
                    kakaoProfileRes.getKakao_account().getEmail(),
                    SocialType.KAKAO,
                    oauthTokenRes.getRefresh_token()
            );

            redisService.saveObject("TMP_RT:KAKAO:" + kakaoProfileRes.getId(), tempData, ttl);

            return new ResponseEntity(CommonRes.ofSuccess(
                    Map.of(
                            "isNewUser", true,
                            "socialId", kakaoProfileRes.getId(),
                            "email", kakaoProfileRes.getKakao_account().getEmail(),
                            "socialType", SocialType.KAKAO), HttpStatus.CREATED.value(), "추가 정보를 입력해주세요"), HttpStatus.CREATED);
        }
        User user = optionalUser.get();
        String atToken = jwtTokenProvider.createAtToken(user);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of(
                        "isNewUser", false,
                        "id", user.getId(),
                        "token", atToken
                ), HttpStatus.OK.value(), "로그인되었습니다."
        ), HttpStatus.OK);
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", originalUser.getId());
//        loginInfo.put("token", atToken);
//        return new ResponseEntity<>(CommonRes.ofSuccess(loginInfo, HttpStatus.OK.value(), "로그인되었습니다."), HttpStatus.OK);


    }

    @PostMapping("/signup-extra")
    public ResponseEntity<?> signupExtra(@Valid @RequestBody InitalSetReq req) {
        // Redis 키는 로그인 시 저장한 형식과 동일해야 함
        String tmpKey = "TMP_RT:" + req.getSocialType() + ":" + req.getSocialId();

        // Redis에서 OauthTempRes 객체 가져오기
        OauthTempRes tempData = redisService.getObject(tmpKey, OauthTempRes.class);

        if (tempData == null) {
            throw new RuntimeException("세션이 만료되었습니다. 다시 로그인 해주세요.");
        }

        // DB 저장: 로그인 시 저장된 소셜 정보 + 이번에 받은 추가 정보 결합
        User user = userService.saveOauthUserWithExtraInfo(
                tempData.getSocialId(),   // 로그인 시 저장된 소셜 ID
                tempData.getEmail(),      // 로그인 시 저장된 이메일
                req,
                tempData.getSocialType()  // 로그인 시 저장된 소셜 타입
        );

        // Redis 임시 데이터 삭제 후 Refresh Token 정식 키로 이동
        redisService.deleteRefreshToken(tmpKey);
        redisService.saveRefreshToken(
                "RT:" + tempData.getSocialType() + ":" + user.getId(),
                tempData.getRefreshToken(),
                expirationRt * 60L
        );

        // Access Token 발급 후 응답
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "token", jwtTokenProvider.createAtToken(user)
        ));
    }

    // 비밀번호 찾기 :임시비밀번호 발급
    @PostMapping("/lost-password")
    public ResponseEntity<?> wantTempPassword(@RequestBody UserFindPasswordReq req) {
        this.userService.wantTempPassword(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                null,  HttpStatus.OK.value(), "임시비밀번호가 이메일로 전송되었습니다."
        ), HttpStatus.OK);
    }


    // 계정 unlock 풀기
    @PostMapping("/unlock")
    public ResponseEntity<?> unlock(@Valid @RequestBody UserUnlockReq req) {
        this.userService.unlock(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                null,  HttpStatus.OK.value(), "임시비밀번호가 이메일로 전송되었습니다."
        ), HttpStatus.OK);
    }

    // 비밀번호 변경
    @PutMapping("change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserChangePasswordReq req) {
        this.userService.changePassword(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                null, HttpStatus.OK.value(), "비밀번호가 변경되었습니다."
        ), HttpStatus.OK);
    }

    //계정 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody UserCheckPasswordReq dto) {
        this.userService.deleteAccount(dto);
        return new ResponseEntity<>(CommonRes.ofSuccess("회원 탈퇴되었습니다.", HttpStatus.OK.value(), "회원탈퇴 완료"), HttpStatus.OK);
    }
    /* ****************마이페이지&설정 관련- (pet) ********************* */
    // 대표동물 설정
    @PutMapping("/my-page/{id}/main-pet")
    public ResponseEntity<?> changeMainPet(@PathVariable Long id) {
        Long mainPetId = this.userService.setMainPet(id);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                "mainPetId :" + mainPetId, HttpStatus.OK.value(), "대표동물 설정"
        ), HttpStatus.OK);
    }
    // 마이페이지 -기본
    @GetMapping("my-page")
    public ResponseEntity<?> enterMyPage() {
        MyPageRes myPageRes = this.userService.enterMyPage();
        return new ResponseEntity<>(CommonRes.ofSuccess(
                myPageRes, HttpStatus.OK.value(), "마이페이지-기본"
        ), HttpStatus.OK);
    }


    /* ******************** 관리자 기능 ******************** */
    // 탈퇴하지 않은 회원목록 조회
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll() {
        List<UserListRes> userList = this.userService.findAll();
        return new ResponseEntity<>(CommonRes.ofSuccess(userList, HttpStatus.OK.value(), "회원 목록 조회 완료"), HttpStatus.OK);
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        UserDetailRes user = this.userService.findById(id);
        return new ResponseEntity<>(CommonRes.ofSuccess(user, HttpStatus.OK.value(), "회원 상세 조회 완료"), HttpStatus.OK);
    }
}
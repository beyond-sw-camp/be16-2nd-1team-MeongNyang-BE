package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.common.security.JwtTokenProvider;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.dto.oauth2.*;
import com.beyond.meongnyang.user.entity.SocialType;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.service.GoogleLoginService;
import com.beyond.meongnyang.user.service.KakaoLoginService;
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
@RequestMapping("/users")
public class UserRestController {
    // 완전 저장
    @Value("${jwt.expirationRt}")
    Long expirationRt;

    public final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleLoginService googleLoginService;
    private final KakaoLoginService kakaoLoginService;

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

    // 회원가입
    @PostMapping("/sign")
    public ResponseEntity<?> save(@Valid @RequestBody UserCreateReq dto) {
        this.userService.save(dto);
        return new ResponseEntity<>(CommonRes.ofSuccess(dto, HttpStatus.CREATED.value(), "회원가입이 완료되었습니다."), HttpStatus.CREATED);
    }

    //  일반 로그인
    @PostMapping("/login")
    public ResponseEntity<?> accessLogin(@Valid @RequestBody UserLoginReq request) {
        User user = this.userService.accessLogin(request);

        String atToken = jwtTokenProvider.createAtToken(user);
        String rtToken = jwtTokenProvider.createRtToken(user); // 내부에서 Redis 저장

        UserLoginRes res = UserLoginRes.builder()
                .accessToken(atToken)
                .refreshToken(rtToken)
                .build();

        return ResponseEntity.ok(
                CommonRes.ofSuccess(res, HttpStatus.OK.value(), "로그인되었습니다.")
        );
    }

    //  구글 로그인
    @PostMapping("/login/google")
    public ResponseEntity<?> googleLogin(@Valid @RequestBody RedirectReq redirectReq) {
        GoogleOauthTokenRes tokenRes = googleLoginService.getAccessToken(redirectReq.getCode());
        GoogleProfileRes profile = googleLoginService.getGoogleProfile(tokenRes.getAccess_token());

        String socialId = profile.getSub();
        String email = profile.getEmail();

        Optional<User> optionalSocial = userService.getUserBySocailId(socialId);
        if (optionalSocial.isPresent()) {
            User user = optionalSocial.get();
            String atToken = jwtTokenProvider.createAtToken(user);
            String rtToken = jwtTokenProvider.createRtToken(user);
            return ResponseEntity.ok(
                    CommonRes.ofSuccess(
                            Map.of("isNewUser", false, "id", user.getId(), "accessToken", atToken, "refreshToken", rtToken),
                            HttpStatus.OK.value(), "로그인되었습니다."
                    )
            );
        }

        Optional<User> optionalEmail = userService.getUserByEmail(email);
        if (optionalEmail.isPresent()) {
            User user = optionalEmail.get();
            userService.linkSocialAccount(user.getId(), SocialType.GOOGLE, socialId);
            String atToken = jwtTokenProvider.createAtToken(user);
            String rtToken = jwtTokenProvider.createRtToken(user);
            return ResponseEntity.ok(
                    CommonRes.ofSuccess(
                            Map.of("isNewUser", false, "id", user.getId(), "accessToken", atToken, "refreshToken", rtToken),
                            HttpStatus.OK.value(), "연동 후 로그인되었습니다."
                    )
            );
        }

        // 신규가입 티켓
        String newUser = jwtTokenProvider.createSignup(socialId, email, SocialType.GOOGLE.name());
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of("isNewUser", true, "signup", newUser, "email", email, "socialType", SocialType.GOOGLE),
                HttpStatus.CREATED.value(), "추가 정보를 입력해주세요"
        ), HttpStatus.CREATED);
    }

    //  카카오 로그인
    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoLogin(@Valid @RequestBody RedirectReq redirectReq) {
        KakaoOauthTokenRes tokenRes = kakaoLoginService.getAccessToken(redirectReq.getCode());
        KakapProfileRes profile = kakaoLoginService.getKakaoProfile(tokenRes.getAccess_token());

        String socialId = profile.getId();
        String email = profile.getKakao_account().getEmail();

        Optional<User> optionalSocial = userService.getUserBySocailId(socialId);
        if (optionalSocial.isPresent()) {
            User user = optionalSocial.get();
            String atToken = jwtTokenProvider.createAtToken(user);
            String rtToken = jwtTokenProvider.createRtToken(user);
            return ResponseEntity.ok(
                    CommonRes.ofSuccess(
                            Map.of("isNewUser", false, "id", user.getId(), "accessToken", atToken, "refreshToken", rtToken),
                            HttpStatus.OK.value(), "로그인되었습니다."
                    )
            );
        }

        Optional<User> optionalEmail = userService.getUserByEmail(email);
        if (optionalEmail.isPresent()) {
            User user = optionalEmail.get();
            userService.linkSocialAccount(user.getId(), SocialType.KAKAO, socialId);
            String atToken = jwtTokenProvider.createAtToken(user);
            String rtToken = jwtTokenProvider.createRtToken(user);
            return ResponseEntity.ok(
                    CommonRes.ofSuccess(
                            Map.of("isNewUser", false, "id", user.getId(), "accessToken", atToken, "refreshToken", rtToken),
                            HttpStatus.OK.value(), "연동 후 로그인되었습니다."
                    )
            );
        }

        String ticket = jwtTokenProvider.createSignup(socialId, email, SocialType.KAKAO.name());
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of("isNewUser", true, "signupTicket", ticket, "email", email, "socialType", SocialType.KAKAO),
                HttpStatus.CREATED.value(), "추가 정보를 입력해주세요"
        ), HttpStatus.CREATED);
    }

    //  추가정보 완료 후 로그인
    @PostMapping("/signup-extra")
    public ResponseEntity<?> signupExtra(@Valid @RequestBody SignupExtraReq req) {
        JwtTokenProvider.SignupTicket ticket = jwtTokenProvider.parseSignup(req.getSignupTicket());

        InitalSetReq extra = new InitalSetReq();
        extra.setName(req.getName());
        extra.setNickname(req.getNickname());

        User user = userService.saveOauthUserWithExtraInfo(
                ticket.socialId(), ticket.email(), extra, SocialType.valueOf(ticket.socialType())
        );

        String atToken = jwtTokenProvider.createAtToken(user);
        String rtToken = jwtTokenProvider.createRtToken(user);

        return ResponseEntity.ok(
                CommonRes.ofSuccess(
                        Map.of("id", user.getId(), "accessToken", atToken, "refreshToken", rtToken),
                        HttpStatus.OK.value(), "연동 후 로그인되었습니다."
                )
        );
    }

    // AT 재발급 — 헤더에서 RT 수신, 새 AT만 반환
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "X-Refresh-Token", required = false) String rtToken) {
        if (rtToken == null || rtToken.isBlank()) {
            return ResponseEntity.status(401).body(CommonRes.ofFailure(HttpStatus.UNAUTHORIZED.value(), "RT 헤더가 없습니다."));
        }
            //  validateRt + createAtToken
            try {
                User user = jwtTokenProvider.validateRefreshToken(rtToken); // Redis 대조
                String newAt = jwtTokenProvider.createAtToken(user);
                return ResponseEntity.ok(CommonRes.ofSuccess(Map.of("accessToken", newAt), HttpStatus.OK.value(), "AT 재발급"));
            } catch (Exception ex) {
                return ResponseEntity.status(401).body(CommonRes.ofFailure(HttpStatus.UNAUTHORIZED.value(), "RT가 올바르지 않거나 만료되었습니다."));
            }
        }

    //  로그아웃 — 헤더 RT로 Redis 제거
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "X-Refresh-Token", required = false) String rtToken) {
        if (rtToken != null && !rtToken.isBlank()) {
            try {
                // email redis에서 꺼내기
                String subject = jwtTokenProvider.getSubjectFromRefresh(rtToken);
                jwtTokenProvider.revokeRefreshToken(subject); // 내부에서 Redis 키 삭제
            } catch (Exception ignore) { /* 이미 만료/위조면 무시 */ }
        }
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "로그아웃 완료"));
    }



    // 비밀번호 찾기 & 잠금처리 초기화 :임시비밀번호 발급
    @PostMapping("/lost-password")
    public ResponseEntity<?> wantTempPassword(@RequestBody UserUnlockReq req) {
        this.userService.unlock(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                null,  HttpStatus.OK.value(), "임시비밀번호가 이메일로 전송되었습니다."
        ), HttpStatus.OK);
    }


    // 비밀번호 변경
    @PutMapping("/change/password")
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
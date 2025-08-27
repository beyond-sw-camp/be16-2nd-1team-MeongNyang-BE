package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.common.security.JwtTokenProvider;
import com.beyond.meongnyang.post.service.PostService;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.dto.oauth2.*;
import com.beyond.meongnyang.user.entity.SocialType;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.service.GoogleLoginService;
import com.beyond.meongnyang.user.service.KakaoLoginService;
import com.beyond.meongnyang.user.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private final PostService postService;

    // 헤더로 rt 응답 공통부분
    private ResponseEntity<?> okWithRtHeader(Object body, String refreshToken) {
        return ResponseEntity.ok()
                .header("X-Refresh-Token", refreshToken)
                .body(body);
    }


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

    @PostMapping("/login")
    public ResponseEntity<?> accessLogin(@Valid @RequestBody UserLoginReq request) {
        User user = this.userService.accessLogin(request);

        String accessToken = jwtTokenProvider.createAtToken(user);
        String refreshToken = jwtTokenProvider.createRtToken(user);
        Map<String, Object> body = Map.of("id", user.getId(), "accessToken", accessToken);
        return okWithRtHeader(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "로그인되었습니다."), refreshToken);
    }


//    public User accessLogin(UserLoginReq request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다."));
//
//        if ("Y".equals(user.getDelYn())) {
//            throw new IllegalArgumentException("사용하지 않는 계정입니다.");
//        }
//        if ("Y".equals(user.getIsLocked())) {
//            throw new IllegalArgumentException("잠긴 계정입니다.");
//        }
//

    private boolean isYes(String yn) {
        return yn != null && yn.trim().equalsIgnoreCase("Y");
    }

    // 구글 로그인
    @PostMapping("/login/google")
    public ResponseEntity<?> googleLogin(@Valid @RequestBody RedirectReq redirectReq) {
        GoogleOauthTokenRes tokenRes = googleLoginService.getAccessToken(redirectReq.getCode());
        GoogleProfileRes profile = googleLoginService.getGoogleProfile(tokenRes.getAccess_token());

        String socialId = profile.getSub();
        String email = profile.getEmail();

        // socialId로 이미 연동된 계정 → 즉시 로그인
        Optional<User> optionalSocial = userService.getUserBySocailId(socialId);
        if (optionalSocial.isPresent()) {
            User user = optionalSocial.get();
            if (isYes(user.getDelYn()))     throw new IllegalArgumentException("사용하지 않는 계정입니다.");
            if (isYes(user.getIsLocked()))  throw new IllegalArgumentException("잠긴 계정입니다.");

            if (user.getSocialType() != SocialType.GOOGLE) {
                throw new EntityExistsException("이미 다른 방식으로 연동된 계정입니다.");
            }

            String accessToken = jwtTokenProvider.createAtToken(user);
            String refreshToken = jwtTokenProvider.createRtToken(user);
            Map<String, Object> body = Map.of("isNewUser", false, "id", user.getId(), "accessToken", accessToken);
            return okWithRtHeader(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "로그인되었습니다."), refreshToken);
        }

        // 이메일 매칭 → 연동 확인 필요
        Optional<User> optionalEmail = userService.getUserByEmail(email);
        if (optionalEmail.isPresent()) {
            User user = optionalEmail.get();
            if (isYes(user.getDelYn()))     throw new IllegalArgumentException("사용하지 않는 계정입니다.");
            if (isYes(user.getIsLocked()))  throw new IllegalArgumentException("잠긴 계정입니다.");
            if (user.getSocialType() != SocialType.COMMON) {
                throw new EntityExistsException("이미 소셜 연동된 계정입니다.");
            }

            String linkTicket = jwtTokenProvider.createTicket(socialId, email, SocialType.GOOGLE.name());

            Map<String, Object> body = Map.of(
                    "needLink", true,
                    "email", email,
                    "socialType", SocialType.GOOGLE,
                    "linkTicket", linkTicket,
                    "message", "기존 이메일 계정과 구글 계정을 연동하시겠습니까?"
            );
            return new ResponseEntity<>(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "연동 확인 필요"), HttpStatus.OK);
        }

        // 신규가입
        String signupTicket = jwtTokenProvider.createTicket(socialId, email, SocialType.GOOGLE.name());
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of("isNewUser", true, "signupTicket", signupTicket, "email", email, "socialType", SocialType.GOOGLE),
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

        // 1) socialId 매칭 → 즉시 로그인
        Optional<User> optionalSocial = userService.getUserBySocailId(socialId);
        if (optionalSocial.isPresent()) {
            User user = optionalSocial.get();
            if (isYes(user.getDelYn()))     throw new IllegalArgumentException("사용하지 않는 계정입니다.");
            if (isYes(user.getIsLocked()))  throw new IllegalArgumentException("잠긴 계정입니다.");

            if (user.getSocialType() != SocialType.KAKAO) {
                throw new EntityExistsException("이미 다른 방식으로 연동된 계정입니다.");
            }

            String accessToken = jwtTokenProvider.createAtToken(user);
            String refreshToken = jwtTokenProvider.createRtToken(user);
            Map<String, Object> body = Map.of("isNewUser", false, "id", user.getId(), "accessToken", accessToken);
            return okWithRtHeader(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "로그인되었습니다."), refreshToken);
        }

        // 2) 이메일 매칭 → 연동 확인 필요
        Optional<User> optionalEmail = userService.getUserByEmail(email);
        if (optionalEmail.isPresent()) {
            User user = optionalEmail.get();

            if (isYes(user.getDelYn()))     throw new IllegalArgumentException("사용하지 않는 계정입니다.");
            if (isYes(user.getIsLocked()))  throw new IllegalArgumentException("잠긴 계정입니다.");
            if (user.getSocialType() != SocialType.COMMON) {
                throw new EntityExistsException("이미 소셜 연동된 계정입니다.");
            }

            String linkTicket = jwtTokenProvider.createTicket(socialId, email, SocialType.KAKAO.name());
            Map<String, Object> body = Map.of(
                    "needLink", true,
                    "email", email,
                    "socialType", SocialType.KAKAO,
                    "linkTicket", linkTicket,
                    "message", "기존 이메일 계정과 카카오 계정을 연동하시겠습니까?"
            );
            return new ResponseEntity<>(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "연동 확인 필요"), HttpStatus.OK);
        }

        // 3) 신규가입
        String signupTicket = jwtTokenProvider.createTicket(socialId, email, SocialType.KAKAO.name());
        return new ResponseEntity<>(CommonRes.ofSuccess(
                Map.of("isNewUser", true, "signupTicket", signupTicket, "email", email, "socialType", SocialType.KAKAO),
                HttpStatus.CREATED.value(), "추가 정보를 입력해주세요"
        ), HttpStatus.CREATED);
    }

    // 연동 확인
    @PostMapping("/link/confirm")
    public ResponseEntity<?> confirmLink(@RequestBody LinkConfirmReq req) {
        JwtTokenProvider.Ticket linkTicket = jwtTokenProvider.parseTicket(req.getLinkTicket());

        User user = userService.getUserByEmail(linkTicket.email()).orElseThrow(
                () -> new EntityNotFoundException("없는 사용자입니다.")
        );

        if (user.getSocialType() != SocialType.COMMON) {
            throw new EntityExistsException("이미 소셜 연동된 계정입니다.");
        }

        userService.linkSocialAndDisablePassword(
                user.getId(),
                SocialType.valueOf(linkTicket.socialType()),
                linkTicket.socialId()
        );

        String accessToken = jwtTokenProvider.createAtToken(user);
        String refreshToken = jwtTokenProvider.createRtToken(user);

        Map<String, Object> body = Map.of("id", user.getId(), "accessToken", accessToken, "message", "연동 완료 및 로그인");
        return okWithRtHeader(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "연동 완료"), refreshToken);
    }


        //  추가정보 완료 후 로그인
        @PostMapping("/signup-extra")
        public ResponseEntity<?> signupExtra(@Valid @RequestBody SignupExtraReq req) {
            JwtTokenProvider.Ticket ticket = jwtTokenProvider.parseTicket(req.getSignupTicket());

            InitalSetReq extra = new InitalSetReq();
            extra.setName(req.getName());
            extra.setNickname(req.getNickname());

            User user = userService.saveOauthUserWithExtraInfo(
                    ticket.socialId(), ticket.email(), extra, SocialType.valueOf(ticket.socialType())
            );

            String accessToken = jwtTokenProvider.createAtToken(user);
            String refreshToken = jwtTokenProvider.createRtToken(user);

            Map<String, Object> body = Map.of("id", user.getId(), "accessToken", accessToken);
            return okWithRtHeader(CommonRes.ofSuccess(body, HttpStatus.OK.value(), "연동 후 로그인되었습니다."), refreshToken);
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
    public ResponseEntity<?> deleteAccount(
            @RequestHeader(value = "X-Refresh-Token", required = false) String rtToken) {

        // 현재 로그인 사용자 기준으로 즉시 탈퇴
        userService.deleteAccount();

        // 세션/RT 정리(있으면)
        if (rtToken != null && !rtToken.isBlank()) {
            try {
                String subject = jwtTokenProvider.getSubjectFromRefresh(rtToken);
                jwtTokenProvider.revokeRefreshToken(subject);
            } catch (Exception ignore) { /* 이미 만료/위조면 무시 */ }
        }

        return ResponseEntity.ok(
                CommonRes.ofSuccess("회원 탈퇴되었습니다.", HttpStatus.OK.value(), "회원탈퇴 완료")
        );
    }

    @GetMapping("/profileImage")
    public ResponseEntity<?> getUserProfileImage(@RequestBody EmailReq req) {
        UserProfileImageRes profileImageRes = userService.findProfileImage(req.getEmail());
        return new ResponseEntity<>(CommonRes.ofSuccess(
                profileImageRes, HttpStatus.OK.value(), "프로필 조회가 완료되었습니다."
        ), HttpStatus.OK);
    }
    /* ****************마이페이지&설정 관련- (pet) ********************* */
    // 대표동물 설정
    @PutMapping("/pets/main")
    public ResponseEntity<?> changeMainPet() {
        Long mainPetId = userService.setMainPet();
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

    // UserRestController.java에 추가
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateReq request) {
        ProfileUpdateRes response = this.userService.updateProfile(request);
        return new ResponseEntity<>(CommonRes.ofSuccess(
                response, HttpStatus.OK.value(), "프로필 수정 완료"
        ), HttpStatus.OK);
    }


    // 팔로우
    @PostMapping("/follows/{id}")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> follow(@PathVariable("id") Long id){
        userService.follow(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("팔로우 완료", HttpStatus.OK.value(), "팔로우를 성공했습니다."), HttpStatus.OK);
    }

    // 언팔로우
    @DeleteMapping("/follows/{id}")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> unFollow(@PathVariable("id") Long id){
        userService.unFollow(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("언팔로우 완료", HttpStatus.OK.value(), "언팔로우를 성공했습니다."), HttpStatus.OK);
    }

    // 팔로우 상태 조회
    @GetMapping("/follows/{id}/status")
    public ResponseEntity<?> checkFollowStatus(@PathVariable("id") Long followingId) {
        boolean isFollowing = userService.checkFollowStatus(followingId);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        Map.of("isFollowing", isFollowing),
                        HttpStatus.OK.value(),
                        "팔로우 상태를 확인했습니다."
                ),
                HttpStatus.OK
        );
    }

    // 내 팔로워 목록 조회
    @GetMapping("/follows/followers")
    public ResponseEntity<?> getMyFollowers(
            @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(userService.getFollowers(pageable, null), HttpStatus.OK.value(), "팔로워 목록 조회 완료"),
                HttpStatus.OK
        );
    }

    // 내 팔로잉 목록 조회
    @GetMapping("/follows/followings")
    public ResponseEntity<?> getMyFollowings(
            @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(userService.getFollowings(pageable, null), HttpStatus.OK.value(), "팔로잉 목록 조회 완료"),
                HttpStatus.OK
        );
    }

    // 팔로워 목록 조회
        @GetMapping("/{id}/follows/followers")
    public ResponseEntity<?> getFollowers(
            @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("id") Long userId) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(userService.getFollowers(pageable, userId), HttpStatus.OK.value(), "팔로워 목록 조회 완료"),
                HttpStatus.OK
        );
    }

    // 팔로잉 목록 조회
    @GetMapping("/{id}/follows/followings")
    public ResponseEntity<?> getFollowings(
            @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("id") Long userId) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(userService.getFollowings(pageable, userId), HttpStatus.OK.value(), "팔로잉 목록 조회 완료"),
                HttpStatus.OK
        );
    }

    // 차단
    @PostMapping("/blocks/{id}")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("차단 완료", HttpStatus.OK.value(), "회원 차단 완료"), HttpStatus.OK);
    }

    // 차단해제
    @DeleteMapping("/blocks/{id}")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> unBlockUser(@PathVariable Long id) {
        userService.unBlockUser(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("차단 해제 완료", HttpStatus.OK.value(), "차단 해제 완료"), HttpStatus.OK);
    }

    // 차단 목록 조회
    @GetMapping("/blocks")
    public ResponseEntity<?> blockList(@PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam("type") String name){
        return new ResponseEntity<>(CommonRes.ofSuccess(userService.blockUsers(name, pageable), HttpStatus.OK.value(), "차단 목록을 조회했습니다."), HttpStatus.OK);
    }

    // 사용자 일기 목록 조회
    @GetMapping("/{id}/posts")
    public ResponseEntity<?> userPostList(@PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable("id") Long id) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.posts(pageable, id),
                        HttpStatus.OK.value(),
                        "일기 목록을 불러왔습니다."
                ), HttpStatus.OK
        );
    }
}
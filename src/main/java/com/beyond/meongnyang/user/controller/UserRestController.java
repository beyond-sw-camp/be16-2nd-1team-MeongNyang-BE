package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.common.security.JwtTokenProvider;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.service.SendEmailService;
import com.beyond.meongnyang.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserRestController {

    public final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
//    // 이메일 찾기
//    @PostMapping("/find/email")
//    public ResponseEntity<?> findEmail(@Valid @RequestBody UserFindEmailReq dto) {
//        String email = this.userService.findEmail(dto);
//        return new ResponseEntity<>(CommonRes.ofSuccess(email, HttpStatus.OK.value(), "이메일을 찾았습니다."), HttpStatus.OK);
//    }

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
package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.dto.ResponseDto;
import com.beyond.meongnyang.common.security.JwtTokenProvider;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.dto.UserCreateDto;
import com.beyond.meongnyang.user.dto.UserFindDto;
import com.beyond.meongnyang.user.dto.UserLoginRequest;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailDto;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameDto;
import com.beyond.meongnyang.user.dto.check.UserCheckPasswordDto;
import com.beyond.meongnyang.user.dto.check.UserCheckPhoneDto;
import com.beyond.meongnyang.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserRestController {

    public final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@Valid @RequestBody UserCheckEmailDto dto) {
        this.userService.checkEmail(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess(dto.getEmail(), HttpStatus.OK.value(), "사용가능한 이메일입니다."), HttpStatus.OK);
    }
    @PostMapping("/check-nickname")
    public ResponseEntity<?> checkNickname (@Valid @RequestBody UserCheckNicknameDto dto) {
        this.userService.checkNickname(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess(dto.getNickname(), HttpStatus.OK.value(), "사용가능한 사용자명입니다."), HttpStatus.OK);
    }

    @PostMapping("/check-phone")
    public ResponseEntity<?> checkPhone(@Valid @RequestBody UserCheckPhoneDto dto) {
        this.userService.checkPhone(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess(dto.getPhone(), HttpStatus.OK.value(), "사용가능한 전화번호입니다."), HttpStatus.OK);
    }


    @PostMapping("/sign")
    public ResponseEntity<?> save(@Valid @RequestBody UserCreateDto dto) {
        this.userService.save(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess(dto, HttpStatus.CREATED.value(), "회원가입이 완료되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> accessLogin(@Valid @RequestBody UserLoginRequest request) {
        this.userService.accessLogin(request);
        User user = this.userService.accessLogin(request);
        String token = jwtTokenProvider.createAtToken(user);
        return new ResponseEntity<>(ResponseDto.ofSuccess(token, HttpStatus.OK.value(), "로그인되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/find/email")
    public ResponseEntity<?> findEmail(@Valid @RequestBody UserFindDto dto){
        String email = this.userService.findEmail(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess(email, HttpStatus.OK.value(), "이메일을 찾았습니다."), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody UserCheckPasswordDto dto) {
        this.userService.deleteAccount(dto);
        return new ResponseEntity<>(ResponseDto.ofSuccess("회원 탈퇴되었습니다.", HttpStatus.OK.value(), "회원탈퇴 완료"), HttpStatus.OK);
    }
}

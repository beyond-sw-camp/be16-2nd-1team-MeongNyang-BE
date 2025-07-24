package com.beyond.meongnyang.user.controller;

import com.beyond.meongnyang.common.dto.CommonDto;
import com.beyond.meongnyang.security.JwtTokenProvider;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.dto.UserCreateDto;
import com.beyond.meongnyang.user.dto.UserFindDto;
import com.beyond.meongnyang.user.dto.UserLoginRequest;
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

    @PostMapping("/sign")
    public ResponseEntity<?> save(@Valid @RequestBody UserCreateDto dto) {
        this.userService.save(dto);
        return new ResponseEntity<>(new CommonDto(dto, HttpStatus.CREATED.value(), "회원가입이 완료되었습니다."), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> accessLogin(@Valid @RequestBody UserLoginRequest request) {
        this.userService.accessLogin(request);
        User user = this.userService.accessLogin(request);
        String token = jwtTokenProvider.createAtToken(user);
        return new ResponseEntity<>(new CommonDto(token, HttpStatus.OK.value(), "로그인되었습니다."), HttpStatus.OK);
    }

    @PostMapping("/find/email")
    public ResponseEntity<?> findEmail(@Valid @RequestBody UserFindDto dto){
        String email = this.userService.findEmail(dto);
        return new ResponseEntity<>(new CommonDto(email, HttpStatus.OK.value(), "이메일을 찾았습니다."), HttpStatus.OK);
    }
}

package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.dto.UserCreateReq;
import com.beyond.meongnyang.user.dto.UserFindEmailReq;
import com.beyond.meongnyang.user.dto.UserLoginReq;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailReq;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPasswordReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPhoneReq;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    //회원 가입 시 이메일, 전화번호, 닉네임 각각 인증
    public void checkEmail(UserCheckEmailReq dto) {
        Optional<User> optionalUser = this.userRepository.findByEmail(dto.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getDelYn().equals("Y")) {
                throw new EntityExistsException("탈퇴한 사용자이메일입니다.");
            }
            throw new EntityExistsException("이미 사용중인 이메일입니다.");
        }
    }
    public void checkNickname(UserCheckNicknameReq dto) {
        Optional<User> optionalUser = this.userRepository.findByNickname(dto.getNickname());
        if(optionalUser.isPresent()) {
           User user = optionalUser.get();
            if(user.getDelYn().equals("Y")) {
                throw new EntityExistsException("탈퇴한 사용자명입니다.");
            }
            throw new EntityExistsException("이미 사용중인 사용자명입니다.");
        }
    }

    public void checkPhone (UserCheckPhoneReq dto) {
        Optional<User> optionalUser = this.userRepository.findByPhone(dto.getPhone());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getDelYn().equals("Y")) {
                throw new EntityExistsException("탈퇴한 전화번호입니다.");
            }
            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
        }

    }
    // 회원가입
    public void save(UserCreateReq dto) {
        String encodedPassword = this.passwordEncoder.encode(dto.getPassword());
        User user = dto.toCreateEntity(encodedPassword);
        this.userRepository.save(user);

    }

    // 로그인
    public User accessLogin(UserLoginReq request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        boolean check = true;
        if(!optionalUser.isPresent()) {
            check = false;
        } else {
            if(!passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())){
                check = false;
            }
        }
        if(!check) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다.");
        }
        if(optionalUser.get().getDelYn().equals("Y"))  {
            throw new IllegalArgumentException("사용하지 않는 계정입니다.");
        }
        return optionalUser.get();
    }

    // 이메일 찾기
    // TODO: repo에서 삭제 하기
    public String findEmail(UserFindEmailReq dto) {
        User user = this.userRepository.findByPhone(dto.getPhone()).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 전화번호입니다."));
        if(!user.getName().equals(dto.getName())) {
            throw new EntityNotFoundException("이름이 일치하지 않습니다.");
        }
        return user.getEmail();
    }

//    // 임시비밀번호 발급 -> 복호화가 안 된다!
//    public String findPassword(UserFindDto dto) {
//        User user = this.userRepository.findByEmail(dto.getEmail()).orElseThrow(() ->new EntityNotFoundException("등록되지 않은 이메일입니다."));
//        if(!user.getPhone().equals(dto.getPhone())){
//            throw new EntityNotFoundException("전화번호가 일치하지 않습니다.");
//        }
//    }

    // 계정 삭제
    public void deleteAccount(UserCheckPasswordReq dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.softDelete();
    }
}

package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.dto.UserCreateDto;
import com.beyond.meongnyang.user.dto.UserFindDto;
import com.beyond.meongnyang.user.dto.UserLoginRequest;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailDto;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameDto;
import com.beyond.meongnyang.user.dto.check.UserCheckPasswordDto;
import com.beyond.meongnyang.user.dto.check.UserCheckPhoneDto;
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
    public void checkEmail(UserCheckEmailDto dto) {
        if(this.userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 이메일입니다.");
        }
    }
    public void checkNickname(UserCheckNicknameDto dto) {
        if(this.userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 사용자명입니다.");
        }
    }

    public void checkPhone (UserCheckPhoneDto dto) {
        if (this.userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
        }
    }
    // 회원가입
    public void save(UserCreateDto dto) {
        // 1. 이메일, 전화번호, 닉네임 중복 인증
        if(this.userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 이메일입니다.");
        }
        if (this.userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
        }
        if(this.userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 사용자명입니다.");
        }
        // 중복없는 true값 db에 저장(회원가입)
        String encodedPassword = this.passwordEncoder.encode(dto.getPassword());
        User user = dto.toCreateEntity(encodedPassword);
        this.userRepository.save(user);

    }

    // 로그인
    public User accessLogin(UserLoginRequest request) {
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
        return optionalUser.get();
    }

    // 이메일 찾기
    // TODO: repo에서 삭제 하기
    public String findEmail(UserFindDto dto) {
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
    public void deleteAccount(UserCheckPasswordDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.softDelete();
    }
}

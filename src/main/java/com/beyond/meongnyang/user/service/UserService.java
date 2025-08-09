package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.user.entity.Follow;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailReq;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPasswordReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPhoneReq;
import com.beyond.meongnyang.user.entity.UserBlock;
import com.beyond.meongnyang.user.repository.FollowRepository;
import com.beyond.meongnyang.user.repository.UserBlockRepository;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserBlockRepository userBlockRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonService commonService;


    //회원 가입 시 이메일, 전화번호, 닉네임 각각 인증
    public void checkEmail(UserCheckEmailReq dto) {
        Optional<User> optionalUser = this.userRepository.findByEmail(dto.getEmail());
        if(optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            if(user.getDelYn().equals("Y")) {
//                throw new EntityExistsException("탈퇴한 사용자이메일입니다.");
//            }
            throw new EntityExistsException("이미 사용중인 이메일입니다.");
        }
    }
    public void checkNickname(UserCheckNicknameReq dto) {
        Optional<User> optionalUser = this.userRepository.findByNickname(dto.getNickname());
        if(optionalUser.isPresent()) {
//           User user = optionalUser.get();
//            if(user.getDelYn().equals("Y")) {
//                throw new EntityExistsException("탈퇴한 사용자명입니다.");
//            }
            throw new EntityExistsException("이미 사용중인 사용자명입니다.");
        }
    }

    public void checkPhone (UserCheckPhoneReq dto) {
        Optional<User> optionalUser = this.userRepository.findByPhone(dto.getPhone());
        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            if(user.getDelYn().equals("Y")) {
//                throw new EntityExistsException("탈퇴한 전화번호입니다.");
//            }
            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
        }

    }
    // 회원가입
    public void save(UserCreateReq dto) {
        // 중복 체크
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 닉네임입니다.");
        }
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
        }
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


    // 계정 삭제
    public void deleteAccount(UserCheckPasswordReq dto) {
        User user = commonService.getCurrentUser();
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.softDelete();
    }

    // 팔로우
    public void follow(Long followingId){
        User follower = commonService.getCurrentUser();
        User following = userRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        if (followRepository.findIdByFollowerAndFollowing(follower, following).isPresent()) {
            throw new EntityExistsException("이미 퍌로우중인 사용자입니다.");

        }
        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();
        followRepository.save(follow);
    }

    // 언팔로우
    public void unFollow(Long followingId){
        User follower = commonService.getCurrentUser();
        User following = userRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        Long followId = followRepository.findByFollowerIdAndFollowId(follower.getId(), following.getId());
        followRepository.deleteById(followId);
    }

    // 팔로우 목록 조회
    public Page<UserFollowDetailRes> followList(String type, Pageable pageable) {
        User user = commonService.getCurrentUser();
        Specification<Follow> followList = (root, query, cb) -> {
            if ("follower".equalsIgnoreCase(type)) {
                return cb.equal(root.get("follower").get("id"), user.getId());
                } else if ("follow".equalsIgnoreCase(type)) {
                return cb.equal(root.get("following").get("id"), user.getId());
            } else {
                throw new IllegalArgumentException("type은 'follower' 또는 'following'만 허용됩니다.");
            }
        };

        return followRepository.findAll(followList, pageable)
                .map(follow -> {
                    // 'follower'이면 나를 팔로우한 사람을, 'follow'이면 내가 팔로우한 사람을 선택
                    User targetUser = "follower".equalsIgnoreCase(type)
                            ? follow.getFollowing()  // 나를 팔로우한 사람
                            : follow.getFollower();    // 내가 팔로우한 사람

                    // UserFollowDetailRes로 변환하여 반환
                    return UserFollowDetailRes.fromEntity(targetUser);
                });
    }

    // 사용자 차단
    public void blockUser(Long blockUserId){
        User user = commonService.getCurrentUser();
        User blockUser = userRepository.findById(blockUserId).orElseThrow(() -> new EntityNotFoundException());
        UserBlock userBlock = UserBlock.builder()
                .user(user)
                .blockUser(blockUser)
                .build();
        userBlockRepository.save(userBlock);
    }

    // 사용자 차단 해제
    public void unBlockUser(Long blockUserId){
        User user = commonService.getCurrentUser();
        Long id = userBlockRepository.findIdByUserIdAndBlockUserId(user.getId(), blockUserId);
        userBlockRepository.deleteById(id);
    }

    // 차단된 사용자 목록 조회
    public Page<UserBlockDetailRes> blockUsers(String name, Pageable pageable){
        User user = commonService.getCurrentUser();
        Page<UserBlock> users;
        if(name == null){
            users = this.userBlockRepository.findAllByUserId(user.getId(), pageable);
        } else {
            users = this.userBlockRepository.findByName(user.getId(), name, pageable);
        }
        return users.map(UserBlockDetailRes::fromEntity);
    }


    /* **************** 관리자 기능 **************** */
    public List<UserListRes> findAll() {
        List<User> users = this.userRepository.findAllBydelYn("N");
        return users.stream().map(UserListRes::fromEntity).toList();

    }

    // 회원 상세조회
    public UserDetailRes findById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 회원입니다."));
        // TODO: 관리자인데 굳이 필요하나?
        if(user.getDelYn().equals("Y")) {
            throw new EntityNotFoundException("탈퇴한 회원입니다.");
        }
        return UserDetailRes.fromEntity(user);
    }
}

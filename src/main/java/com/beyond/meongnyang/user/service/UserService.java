package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.user.entity.UserFollow;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailReq;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPasswordReq;
import com.beyond.meongnyang.user.dto.check.UserCheckPhoneReq;
import com.beyond.meongnyang.user.entity.UserBlock;
import com.beyond.meongnyang.user.repository.FollowRepository;
import com.beyond.meongnyang.user.repository.UserBlockRepository;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.pet.repository.PetRepository;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserBlockRepository userBlockRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonService commonService;
    private final UserLockedService userLockedService;

    private final PetRepository petRepository;
    private final SendEmailService sendEmailService;
    private final EmailVerificationService emailVerificationService;


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

//    public void checkPhone (UserCheckPhoneReq dto) {
//        Optional<User> optionalUser = this.userRepository.findByPhone(dto.getPhone());
//        if (optionalUser.isPresent()) {
////            User user = optionalUser.get();
////            if(user.getDelYn().equals("Y")) {
////                throw new EntityExistsException("탈퇴한 전화번호입니다.");
////            }
//            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
//        }
//    }

    // 이메일 인증코드 발급
    public void sendCode (UserCheckEmailReq req) {
        String unknownEmail = req.getEmail();
        String code  = emailVerificationService.createAndSendCode(unknownEmail);
        sendEmailService.sendVerificationCode(unknownEmail, code);

    }

    // 이메일 인증코드 검증
    public void verifyCode (UserEmailVerifyReq req) {
        String havetoEmail = req.getEmail();
        String code = req.getCode();
        boolean check = false;
        check = this.emailVerificationService.verifyCode(havetoEmail, code);
        if(!check) {
            throw new IllegalArgumentException("이메일 혹은 인증코드가 다릅니다.");
        } else {
            this.emailVerificationService.deleteCode(havetoEmail);
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
//        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
//            throw new EntityExistsException("이미 사용중인 전화번호입니다.");
//        }
        String encodedPassword = this.passwordEncoder.encode(dto.getPassword());
        User user = dto.toCreateEntity(encodedPassword);
        this.userRepository.save(user);

    }

    // 로그인
//    public User accessLogin(UserLoginReq request) {
//            //  존재 확인
//            User user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다."));
//
//            //  상태 체크
//            if ("Y".equals(user.getDelYn())) {
//                throw new IllegalArgumentException("사용하지 않는 계정입니다.");
//            }
//            if ("Y".equals(user.getIsLocked())) {
//                throw new IllegalArgumentException("잠긴 계정입니다.");
//            }
//            //  비밀번호 체크
//            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//                user.updateCount(user.getFailedCount() + 1); // DB에 저장되는 필드
//                userRepository.saveAndFlush(user);
//                if (user.getFailedCount() >= 5) {
//                    user.lockedAccount();
////                    throw new IllegalArgumentException("5번 틀려서 계정이 잠겼습니다.");
//                }
//                throw new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다.");
//            }
//            //  로그인 성공 시 실패 횟수 초기화
//            user.updateCount(0);
//
//            return user;
//        }
    public User accessLogin(UserLoginReq request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다."));

        // 2. 상태 체크
        if ("Y".equals(user.getDelYn())) {
            throw new IllegalArgumentException("사용하지 않는 계정입니다.");
        }
        if ("Y".equals(user.getIsLocked())) {
            throw new IllegalArgumentException("잠긴 계정입니다.");
        }

        // 3. 비밀번호 체크
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int failCount = userLockedService.increaseFailedCount(user.getId());
            int remain = 5 - failCount;

            if(remain <=0) {
                throw new IllegalArgumentException("로그인 시도횟수를 초과하여 계정이 잠겼습니다.");
            }   else {
                throw new IllegalArgumentException("로그인 시도 실패");
            }

        }

        // 4. 로그인 성공 시 실패 횟수 초기화
        userLockedService.resetFailedCount(user.getId());

        return user;
    }


//    // 이메일 찾기
//
//    public String findEmail(UserFindEmailReq dto) {
//        User user = this.userRepository.findByPhone(dto.getPhone()).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 전화번호입니다."));
//        if(!user.getName().equals(dto.getName())) {
//            throw new EntityNotFoundException("이름이 일치하지 않습니다.");
//        }
//        return user.getEmail();
//    }

    // 비밀번호 찾기: 임시 비밀번호 발급
    public void wantTempPassword(UserFindPasswordReq req) {
        User user = this.userRepository.findByNameAndEmail(req.getName(), req.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("등록된 회원정보가 없습니다.")
        );
        String tempPassword = this.userLockedService.generateTempPassword();
        this.sendEmailService.sendTemporaryPassword(req.getEmail(), tempPassword);
    }

    // 계정 락 풀기
    //TODO: 수정 읽음 동시성 문제 해결하기, 비밀번호 변경 , 임시 비밀번호 시간은?
    public void unlock(UserUnlockReq req) {
        User user = this.userRepository.findByNameAndEmail(req.getName(), req.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("등록된 회원정보가 없습니다."));
        if(user.getDelYn().equals("Y")) {
            throw new IllegalArgumentException("이미 탈퇴한 계정입니다.");
        }
        userLockedService.resetFailedCount(user.getId());
        user.unlockedAccount();
        String tempPassword = userLockedService.generateTempPassword();
        user.updatePassword(passwordEncoder.encode(tempPassword));
        sendEmailService.sendTemporaryPassword(req.getEmail(), tempPassword);

    }

    // 비밀번호 변경
    public void changePassword (UserChangePasswordReq req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("등록된 회원정보가 없습니다.")
        );

        boolean check = passwordEncoder.matches(req.getOldPassword(), user.getPassword());
        if(!check) {
            throw new EntityNotFoundException("비밀번호가 틀립니다.");
        }
        if(req.getOldPassword().equals(req.getNewPassword())){
            throw new IllegalArgumentException("기존 비밀번호랑 같습니다.");
        }
        String newPassword = passwordEncoder.encode(req.getNewPassword());
        user.updatePassword(newPassword);
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
        UserFollow userFollow = UserFollow.builder()
                .follower(follower)
                .following(following)
                .build();
        followRepository.save(userFollow);
    }

    // 언팔로우
    public void unFollow(Long followingId) {
        User user = commonService.getCurrentUser();

        if (user.getId().equals(followingId)) {
            throw new IllegalArgumentException("본인은 언팔로우할 수 없습니다.");
        }

        // 대상 사용자 검증 (존재하지 않으면 예외)
        userRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        UserFollow userFollow = followRepository.findByFollowerIdAndFollowingId(user.getId(), followingId)
                .orElseThrow(() -> new EntityNotFoundException("팔로우 관계가 없습니다."));

        followRepository.delete(userFollow);
    }


    // 나를 팔로우하는 사람 목록 (followers)
    public Page<UserFollowRes> getFollowers(Pageable pageable) {
        User user = commonService.getCurrentUser();
        return followRepository.findByFollowing(user, pageable)
                .map(UserFollow::getFollower).map(UserFollowRes::fromEntity);
    }

    // 내가 팔로우하는 사람 목록 (followings)
    public Page<UserFollowRes> getFollowings(Pageable pageable) {
        User user = commonService.getCurrentUser();
        return followRepository.findByFollower(user, pageable)
                .map(UserFollow::getFollowing).map(UserFollowRes::fromEntity);
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

    /* ****************마이페이지&설정 관련-(pet) ********************* */
    // 대표동물 설정
    public Long setMainPet(Long petId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 사용자입니다."));
        Pet pet = this.petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("펫 정보가 없습니다."));
        if(!user.getId().equals(pet.getUser().getId())){
            throw new AccessDeniedException("본인 소유의 반려동물만 대표동물로 등록할 수 있습니다.");
        }
        user.changeMainPet(petId);
        return petId;
    }
    public MyPageRes enterMyPage() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 사용자입니다."));
        Pet mainPet = null;
        // 펫을 등록하지 않은 사용자일 수도 있음
        if(user.getMainPetId() != null) {
            mainPet = this.petRepository.findById(user.getMainPetId()).orElse(null);
        }
        return MyPageRes.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .mainPetId(mainPet != null ? mainPet.getId() : null)
                .mainPetImage(mainPet != null ? mainPet.getPetProfileUrl() : null)
                .build();
    }

    /* **************** 관리자 기능 **************** */
    // 회원 전체 조회 (탈퇴회원포함)
    public Page<UserListRes> findAllUser(Pageable pageable) {
        Page<User> users = this.userRepository.findAll(pageable);
        return users.map(UserListRes::fromEntity);
    }

    // 회원 전체 조회
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

package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.common.service.SseService;
import com.beyond.meongnyang.user.dto.ProfileUpdateRes;
import com.beyond.meongnyang.user.entity.*;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.dto.check.UserCheckEmailReq;
import com.beyond.meongnyang.user.dto.check.UserCheckNicknameReq;
import com.beyond.meongnyang.user.repository.FollowRepository;
import com.beyond.meongnyang.user.repository.UserBlockRepository;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.pet.repository.PetRepository;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.dto.oauth2.InitalSetReq;
import com.beyond.meongnyang.user.entity.SocialType;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserStatus;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.beyond.meongnyang.user.entity.Role.*;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final UserBlockRepository userBlockRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonService commonService;
    private final UserLockedService userLockedService;
    private final SseService sseService;
    private final SendEmailService sendEmailService;
    private final EmailVerificationService emailVerificationService;
    private final EntityManager em;


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
        String encodedPassword = this.passwordEncoder.encode(dto.getPassword());
        User user = dto.toCreateEntity(encodedPassword);
        this.userRepository.save(user);

    }

    // 로그인
    public User accessLogin(UserLoginReq request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 혹은 비밀번호가 다릅니다."));

        if ("Y".equals(user.getDelYn())) {
            throw new IllegalArgumentException("사용하지 않는 계정입니다.");
        }
        if ("Y".equals(user.getIsLocked())) {
            throw new IllegalArgumentException("잠긴 계정입니다.");
        }

        // 소셜 계정은 비번 로그인 금지
        if (user.getSocialType() != SocialType.COMMON || user.getPassword() == null) {
            throw new EntityExistsException("소셜 연동 계정입니다. 소셜 로그인을 사용하세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int failCount = userLockedService.increaseFailedCount(user.getId());
            int remain = 5 - failCount;
            if (remain <= 0) throw new IllegalArgumentException("로그인 시도횟수를 초과하여 계정이 잠겼습니다.");
            throw new IllegalArgumentException("로그인 시도 실패");
        }

        userLockedService.resetFailedCount(user.getId());
        return user;
    }

    // 소셜로 등록된 사용자 조회
    public Optional<User> getUserBySocailId(String socialId) {
        return this.userRepository.findBySocialId(socialId);

    }
    // email로 사용자 조회
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 소셜 계정 연동: 기존 유저(userId)에 socialType/socialId를 세팅
    public void linkSocialAndDisablePassword(Long userId, SocialType socialType, String socialId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        if (user.getSocialType() != SocialType.COMMON) {
            throw new EntityExistsException("이미 소셜 연동된 계정");
        }

        user.updateSocialType(socialType);
        user.updateSocialId(socialId);

        //  이후부터 비번 로그인 금지
        user.updatePassword(null);
    }


    // oauth 로그인 후 추가 정보 등록 후 db 저장.
    public User saveOauthUserWithExtraInfo(String socialId, String email, InitalSetReq extraInfo, SocialType socialType) {
        User user = User.builder()
                .socialId(socialId)
                .email(email)
                .name(extraInfo.getName())
                .nickname(extraInfo.getNickname())
                .socialType(socialType)
                .userStatus(UserStatus.ACTIVE)
                .build();
        return userRepository.save(user);
    }


    // 계정 락 풀기
    //TODO: 수정 읽음 동시성 문제 해결하기, 비밀번호 변경 , 임시 비밀번호 시간은?
    public void unlock(UserUnlockReq req) {
        User user = this.userRepository.findByNameAndEmail(req.getName(), req.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("등록된 회원정보가 없습니다."));
        if(user.getDelYn().equals("Y")) {
            throw new IllegalArgumentException("이미 탈퇴한 계정입니다.");
        }
        user.updateCount(0);                                            // 실패시도 직접 초기화
        user.unlockedAccount();                                         // 잠금 처리 초기화
        String tempPassword = userLockedService.generateTempPassword(); // 임시비밀번호 발급
        user.updatePassword(passwordEncoder.encode(tempPassword));      // 임시비밀번호 암호화

        userRepository.saveAndFlush(user);

        // 커밋 후 메일 발송 예약
        System.out.println("[DEBUG] queuing mail for " + user.getEmail());
        sendEmailService.queueTemporaryPassword(user.getEmail(), tempPassword);
        // 메서드 종료 시 트랜잭션 커밋 → 그 다음 리스너가 발송

    }

    // 비밀번호 변경
    public void changePassword (UserChangePasswordReq req) {
        User user = commonService.getCurrentUser();
        if (user.getPassword() == null || user.getSocialType() != SocialType.COMMON) {
            throw new EntityExistsException("소셜 계정은 비밀번호 변경이 불가합니다.");
        }

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
    public void deleteAccount() {
        User user = commonService.getCurrentUser();
        user.softDelete();
    }

    // user 프로필 이미지 가져오기
    public UserProfileImageRes findProfileImage(String email) {
        String targetEmail = email;
        User targetUser = userRepository.findByEmail(targetEmail).orElseThrow(
                () -> new EntityNotFoundException("해당 사용자가 없습니다.")
        );

        return UserProfileImageRes.builder()
                .petProfileUrl(targetUser.getMainPet() == null ? "" : targetUser.getMainPet().getPetProfileUrl())
                .build();
    }
  
    // 팔로우
    public void follow(Long followingId){
        User follower = commonService.getCurrentUser();
        User following = userRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));
        if (follower.getId().equals(followingId)) {
            throw new IllegalArgumentException("본인은 팔로우할 수 없습니다.");
        }
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

    // 팔로우 상태 확인
    public boolean checkFollowStatus(Long followingId) {
        User follower = commonService.getCurrentUser();
        User following = userRepository.findById(followingId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 본인인지 확인
        if (follower.equals(following)) {
            return false; // 본인은 팔로우할 수 없으므로 false 반환
        }

        // 팔로우 관계 확인
        return followRepository.findIdByFollowerAndFollowing(follower, following)
                .isPresent();
    }


    // 팔로우하는 사람 목록 (followers)
    public Page<UserFollowRes> getFollowers(Pageable pageable, Long userId) {
        User user;
        if(userId != null){
            user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        } else {
            user = commonService.getCurrentUser();
        }
        return followRepository.findByFollowing(user, pageable)
                .map(UserFollow::getFollower).map(UserFollowRes::fromEntity);
    }

    // 내가 팔로우하는 사람 목록 (followings)
    public Page<UserFollowRes> getFollowings(Pageable pageable, Long userId) {
        User user;
        if(userId != null){
            user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        } else {
            user = commonService.getCurrentUser();
        }
        return followRepository.findByFollower(user, pageable)
                .map(UserFollow::getFollowing).map(UserFollowRes::fromEntity);
    }

    // 사용자 차단(사용자가 사용자를 차단)
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

    public Long changeMainPet(Long petId) {
        User user = commonService.getCurrentUser();
        Pet pet = this.petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("펫 정보가 없습니다."));

        if (!pet.getUser().getId().equals(user.getId())) throw new AccessDeniedException("내 펫이 아닙니다.");

        user.changeMainPet(pet);

        return pet.getId();
    }

    public MyPageRes enterMyPage() {
        User user = commonService.getCurrentUser();
        Pet mainPet = user.getMainPet();

        return MyPageRes.builder()
                .name(user.getName())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .mainPetId(mainPet != null ? mainPet.getId() : null)
                .mainPetImage(mainPet != null ? mainPet.getPetProfileUrl() : null)
                .socialType(user.getSocialType())
                .userStatus(user.getUserStatus())
                .build();
    }

    public ProfileUpdateRes updateProfile(ProfileUpdateReq request) {
        User user = commonService.getCurrentUser();
        // 이름과 닉네임 업데이트
        user.updateProfile(request);

        return ProfileUpdateRes.builder()
                .name(request.getName())
                .nickname(request.getNickname())
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
        List<User> users = this.userRepository.findAllByDelYn("N");
        return users.stream().map(UserListRes::fromEntity).toList();
    }

    // 회원 상세조회
    public UserDetailRes findById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 회원입니다."));
        if(user.getDelYn().equals("Y")) {
            throw new EntityNotFoundException("탈퇴한 회원입니다.");
        }
        return UserDetailRes.fromEntity(user);
    }

    // 관리자에 의한 서비스 이용 차단 해제
    public void unbanByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));
        user.unblock();
    }

    // 서비스 이용 차단 및 차단 해제 처리
    public void handleBan(User admin, User user, Role newRole, LocalDateTime expiryDate) {
        // 1) 역할/만료일 갱신
        // 변경 결과에 따라 SSE 이벤트/메시지/만료시각을 준비
        String event = "";
        String message = "";

        switch (newRole) {
            case TEMPORARY_BLOCK -> {
                event = "ban";
                message = "계정이 기간 차단되었습니다.";
                user.updateRole(TEMPORARY_BLOCK);
                user.setBlockExpiryDate(expiryDate);
            }
            case PERMANENT_BLOCK -> {
                event = "ban";
                message = "계정이 영구 차단되었습니다.";
                user.updateRole(PERMANENT_BLOCK);
            }
            case USER -> { // 차단 해제
                event = "unban";
                message = "차단이 해제되었습니다.";
                user.updateRole(USER);
            }
        }
        em.flush();

        // SSE 전송 (message만 보내는 버전)
        sseService.publishMessage(
                event,                 // "ban" | "unban"
                user.getEmail(),       // receiver = 대상 사용자
                message
        );
    }
}

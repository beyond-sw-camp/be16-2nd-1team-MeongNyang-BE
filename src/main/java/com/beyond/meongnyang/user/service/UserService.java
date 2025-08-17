package com.beyond.meongnyang.user.service;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.pet.repository.PetRepository;
import com.beyond.meongnyang.user.dto.check.*;
import com.beyond.meongnyang.user.dto.oauth2.InitalSetReq;
import com.beyond.meongnyang.user.entity.SocialType;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.dto.*;
import com.beyond.meongnyang.user.entity.UserStatus;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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
    public Optional<User> getUserBySocailId(String socialId) {
        return this.userRepository.findBySocialId(socialId);

    }
    // email로 사용자 조회
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 소셜 계정 연동: 기존 유저(userId)에 socialType/socialId를 세팅
    @Transactional
    public void linkSocialAccount(Long userId, SocialType type, String socialId) {
        if (userId == null) throw new IllegalArgumentException("userId 필수");
        if (type == null) throw new IllegalArgumentException("socialType 필수");
        if (socialId == null || socialId.isBlank()) throw new IllegalArgumentException("socialId 필수");

        User user = findEntityById(userId);


        user.updateSocialType(type);
        user.updateSocialId(socialId);
    }

    // 필수 조회용: 없으면 404 성격의 예외
    public User findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("등록되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        user.softDelete();
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
    // 회원 전체 조회
    public List<UserListRes> findAll() {
        List<User> users = this.userRepository.findAllByDelYn("N");
        return users.stream().map(a -> UserListRes.fromEntity(a)).toList();

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

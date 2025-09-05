package com.beyond.meongnyang.user.entity;

import com.beyond.meongnyang.admin.dto.AdminUserUpdateReq;
import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.dto.ProfileUpdateReq;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@ToString
public class User extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // TODO: 사용자 회원가입시 필수 입력값
/********************************************************************************/
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

//    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 255)
    private String name;


    // TODO: nickname 수정 사항 해야함
    @Column(name = "nickname", nullable = false, unique = true, length = 255)
    private String nickname;

/***********************************************************************************/
    // TODO: 역할 초기 설정 넣기
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.APPLICANT;

    //TODO 활동시 point 쌓기, 초기 설정 0으로 잡아두기
    @Column(name = "point", nullable = false)
    @Builder.Default
    private int point = 0;

    @Column(name = "location",nullable = true, length = 255)
    private String location;

    // 계정 잠금 여부 : Y면 계정 잠금 상태
    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private String isLocked = "N";

    // 비밀번호 틀린 횟수 카운트 : 계정 잠금 . 사용
    @Column(name = "failed_count", nullable = false)
    @Builder.Default
    private int failedCount = 0;

    // 변경사항 기록
    //TODO: 비밀번호 틀렸을 시에 계정잠금된 시점 기록.
    @Column(name = "is_locked_at", nullable = true)
    private LocalDateTime isLockedAt;

    // TODO: 비밀번호 변경시각 기록. + 비밀번호 변경 알림일
    @Column(name = "password_change_at", nullable = true)
    private LocalDateTime passwordChangeAt;


    // 탈퇴 시 y로 변경
    @Column(name = "del_yn", nullable = false)
    @Builder.Default
    private String delYn = "N";

    // 탈퇴 시에 기록
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    /* ****************** social Login ******************* */
    @Column(name = "social_id", nullable = true)
    private String socialId;
    @Column(name = "social_type", nullable = true)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SocialType socialType = SocialType.COMMON;
    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus userStatus = UserStatus.ACTIVE;
    // 차단 만료 일시
    @Column(name = "block_expiry_date", nullable = true)
    private LocalDateTime blockExpiryDate;

    // 회원가입 승인 시에 기록
    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;

    /* ******************연관관계***************** */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pet> pets = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_pet_id", nullable = true)
    private Pet mainPet;

    /* ******************메소드******************* */
    // 계정 삭제
    public void softDelete() {
        this.delYn = "Y";
        this.deletedAt = LocalDateTime.now();
    }

    // 잠금 관련 로그인 시도 횟수
    public void updateCount(int count) {
        this.failedCount = count;
    }
    // 계정 잠금
    public void lockedAccount() {
        this.isLocked = "Y";
        this.isLockedAt = LocalDateTime.now();
    }
    // 잠금 해제
    public void unlockedAccount() {
        this.isLocked = "N";
        this.isLockedAt = null;
    }

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 대표동물 설정
    public void changeMainPet(Pet mainPet) {
        this.mainPet = mainPet;
    }

    // 소셜 id update
    public void updateSocialId(String socialId) {
        this.socialId = socialId;
    }
    // 소셜 type update
    public void updateSocialType(SocialType socialType) {
        this.socialType = socialType;
    }


    // 권한 변경
    public void updateRole(Role role){
        this.role = role;
    }

    // 차단 기간 설정(기간 차단)
    public void setBlockExpiryDate(LocalDateTime blockExpiryDate) {
        this.blockExpiryDate = blockExpiryDate;
    }

    // 차단 해제
    public void unblock(){
        this.role = Role.USER;
        this.blockExpiryDate = null;
    }

    // 회원가입 승인
    public void approve() {
        this.approvedAt = LocalDateTime.now();
        this.role = Role.USER;
    }

    // 회원정보 수정
    public void updateUser(AdminUserUpdateReq req) {
        this.name = req.getName();
        this.nickname = req.getNickname();
        this.location = req.getLocation();
        this.point = req.getPoint();
    }

    // 회원 프로필 수정
    public void updateProfile(ProfileUpdateReq req) {
        this.name = req.getName();
        this.nickname = req.getNickname();
    }
}

package com.beyond.meongnyang.user.domain;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "password", nullable = false, unique = true, length = 255)
    private String password;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // TODO: 전화번호로 비밀번호, 이메일 찾기? & 전화번호 입력받는 형싱 000-0000-0000으로 할지 -없이 받을지
    @Column(name = "phone", nullable = false, unique = true, length = 255)
    private String phone;

    // TODO: nickname 수정 사항 해야함
    @Column(name = "nickname", nullable = false, unique = true, length = 255)
    private String nickname;

/***********************************************************************************/
    // TODO: 역할 초기 설정 넣기
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    //TODO 활동시 point 쌓기, 초기 설정 0으로 잡아두기
    @Column(name = "point", nullable = false)
    @Builder.Default
    private int point = 0;

    @Column(name = "location",nullable = true, length = 255)
    private String location;
    // TODO: 간편인증
    @Column(name = "third_party", nullable = true, length = 255)
    private String thirdParty;
    // TODO: 계정 탈퇴시 관련 내용 처리해야 함.
    @Column(name = "is_leaved", nullable = true, length = 255)
    private String isLeaved;

    @Column(name = "is_locked", nullable = true, length = 255)
    private String isLocked;

    // 변경사항 기록
    //TODO: 비밀번호 틀렸을 시에 계정잠금된 시점 기록.
    @Column(name = "is_locked_at", nullable = true)
    private LocalDateTime isLockeAt;

    // TODO: 비밀번호 변경시각 기록. + 비밀번호 변경 알림일
    @Column(name = "password_update_at", nullable = true)
    private LocalDateTime passwordUpdateAt;

    @Column(name = "delyn", nullable = false)
    @Builder.Default
    private String delYn = "N";

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
    /* ***********메소드***********/
    public void softDelete() {
        this.delYn = "Y";
        this.deletedAt = LocalDateTime.now();
    }


}

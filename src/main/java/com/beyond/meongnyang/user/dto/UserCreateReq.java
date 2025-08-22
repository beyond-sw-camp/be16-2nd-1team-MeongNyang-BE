package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserCreateReq {
    @NotEmpty(message = "이메일 입력해주세요.")
    private String email;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "8자리 이상 입력해주세요")
    private String password;
    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "사용하실 사용자명을 입력해주세요")
    private String nickname;

    public User toCreateEntity (String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .name(this.name)
                .nickname(this.nickname)
                .build();
    }
}

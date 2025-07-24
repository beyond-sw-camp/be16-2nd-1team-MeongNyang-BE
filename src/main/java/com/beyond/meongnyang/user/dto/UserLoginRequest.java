package com.beyond.meongnyang.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserLoginRequest {
    @NotEmpty(message = "이메일 입력해주세요.")
    private String email;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "8자리 이상 입력해주세요")
    private String password;
}

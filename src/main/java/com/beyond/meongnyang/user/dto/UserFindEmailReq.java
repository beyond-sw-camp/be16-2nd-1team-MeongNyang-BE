package com.beyond.meongnyang.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserFindEmailReq {
    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "전화번호를 입력해주세요")
    private String phone;
}

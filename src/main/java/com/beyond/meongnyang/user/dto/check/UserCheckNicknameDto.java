package com.beyond.meongnyang.user.dto.check;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCheckNicknameDto {
    @NotEmpty(message = "사용하실 사용자명을 입력해주세요")
    private String nickname;
}

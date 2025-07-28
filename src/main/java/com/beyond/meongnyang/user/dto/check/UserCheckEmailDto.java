package com.beyond.meongnyang.user.dto.check;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCheckEmailDto {
    @NotEmpty(message = "이메일 입력해주세요.")
    private String email;
}

package com.beyond.meongnyang.user.dto.check;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCheckPhoneDto {
    @NotEmpty(message = "전화번호를 입력해주세요")
    private String phone;
}

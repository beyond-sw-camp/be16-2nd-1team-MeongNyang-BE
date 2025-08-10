package com.beyond.meongnyang.user.dto.check;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEmailVerifyReq {
    private String email;
    private String code;
}

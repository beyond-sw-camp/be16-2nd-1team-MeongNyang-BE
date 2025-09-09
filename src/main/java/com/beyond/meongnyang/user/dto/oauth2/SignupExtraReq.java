package com.beyond.meongnyang.user.dto.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupExtraReq {
     private String name;
     private String nickname;
     private String signupTicket;
}

package com.beyond.meongnyang.user.dto.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RedirectReq {
    private String code;
}

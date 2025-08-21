package com.beyond.meongnyang.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateReq {
    private String name;
    private String nickname;
    private String location;
    private int point;
}

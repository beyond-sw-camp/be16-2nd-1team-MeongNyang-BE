package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserFollow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserSummaryRes {
    private String profileImage;
    private String userName;

    public static UserSummaryRes fromEntity(User user){
        return UserSummaryRes.builder()
                .profileImage("아직 이미지가 없어여")
                .userName(user.getName())
                .build();
    }
}

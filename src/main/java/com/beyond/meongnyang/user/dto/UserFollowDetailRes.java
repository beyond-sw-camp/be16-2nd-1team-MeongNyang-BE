package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFollowDetailRes {
    private String profileImage;
    private String userName;

    public static UserFollowDetailRes fromEntity(User user){
        return UserFollowDetailRes.builder()
                .profileImage("아직 이미지가 없어여")
                .userName(user.getName())
                .build();
    }
}

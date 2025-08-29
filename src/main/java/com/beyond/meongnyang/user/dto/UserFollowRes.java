package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserFollow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFollowRes {
    private Long userId;
    private String profileImage;
    private String userName;
    private String userEmail;

    public static UserFollowRes fromEntity(User user) {
        return UserFollowRes.builder()
                .userId(user.getId())
                .profileImage(user.getMainPet() == null ? "" : user.getMainPet().getPetProfileUrl())
                .userName(user.getNickname()) // 프론트 코드가 전부 userName을 받고 있는 중이라 임시로 대체했습니다.
                .userEmail(user.getEmail())
                .build();
    }
}

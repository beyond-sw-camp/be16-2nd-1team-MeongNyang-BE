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
public class UserFollowRes {
    private String profileImage;
    private String userName;
    private String userEmail;

    public static UserFollowRes fromEntity(User user) {
        String profileImage = "";

        if (user.getPets().stream().anyMatch(pet -> pet.getId().equals(user.getMainPetId())))
            profileImage = user.getPets().stream().filter(pet -> pet.getId().equals(user.getMainPetId())).findFirst().get().getPetProfileUrl();

        return UserFollowRes.builder()
                .profileImage(profileImage)
                .userName(user.getName())
                .userEmail(user.getEmail())
                .build();
    }
}

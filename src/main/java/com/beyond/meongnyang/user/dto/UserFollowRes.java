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
    private Long userId;
    private String profileImage;
    private String petName;
    private String userEmail;

    public static UserFollowRes fromEntity(User user) {
        String profileImage = "";
        String petName = "";

        if (user.getPets().stream().anyMatch(pet -> pet.getId().equals(user.getMainPetId()))){
            profileImage = user.getPets().stream().filter(pet -> pet.getId().equals(user.getMainPetId())).findFirst().get().getPetProfileUrl();
            petName = user.getPets().stream().filter(pet -> pet.getId().equals(user.getMainPetId())).findFirst().get().getName();
        }
        if(petName != null && petName.isEmpty()){
            petName = user.getName();
        }

        return UserFollowRes.builder()
                .userId(user.getId())
                .profileImage(profileImage)
                .petName(petName)
                .userEmail(user.getEmail())
                .build();
    }
}

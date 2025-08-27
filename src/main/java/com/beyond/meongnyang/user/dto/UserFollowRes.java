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
        String profileImage = "";
        Optional<Pet> petOptional = user.getPets().stream().filter(pet -> pet.getId().equals(user.getMainPetId())).findFirst();

        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            profileImage = pet.getPetProfileUrl();
        }

        return UserFollowRes.builder()
                .userId(user.getId())
                .profileImage(profileImage)
                .userName(user.getName())
                .userEmail(user.getEmail())
                .build();
    }
}

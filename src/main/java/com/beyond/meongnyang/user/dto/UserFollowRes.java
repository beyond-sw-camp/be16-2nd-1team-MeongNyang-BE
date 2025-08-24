package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.entity.UserFollow;
import jakarta.persistence.EntityNotFoundException;
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

        Pet pet = user.getPets().stream()
                .filter(p -> p.getId().equals(user.getMainPetId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("펫을 찾을 수 없습니다."));
        profileImage = pet.getPetProfileUrl();
        petName = pet.getName();
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

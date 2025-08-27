package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostLikeListRes {
    private Long userId;
    private String profileImage;
    private String userName;
    private String date;

    public static PostLikeListRes fromEntity(Post post, User user){
        String profileImage = "";

        Optional<Pet> petOptional = user.getPets().stream().filter(pet -> pet.getId().equals(user.getMainPetId())).findFirst();

        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            profileImage = pet.getPetProfileUrl();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 hh:mm");
        return PostLikeListRes.builder()
                .userId(user.getId())
                .profileImage(profileImage)
                .userName(user.getName())
                .date(post.getCreatedAt().format(formatter))
                .build();
    }
}

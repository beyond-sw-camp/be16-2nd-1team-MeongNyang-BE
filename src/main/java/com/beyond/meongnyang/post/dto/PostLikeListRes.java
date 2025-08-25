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
    private String petName;
    private String date;

    public static PostLikeListRes fromEntity(Post post, User user){
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 hh:mm");
        return PostLikeListRes.builder()
                .userId(user.getId())
                .profileImage(profileImage)
                .petName(petName)
                .date(post.getCreatedAt().format(formatter))
                .build();
    }
}

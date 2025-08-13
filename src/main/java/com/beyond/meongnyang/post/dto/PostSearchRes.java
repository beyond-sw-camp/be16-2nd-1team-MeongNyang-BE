package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostSearchRes {
    private Long id;
    private String petProfile;
    private String petName;
    private String title;
    private String content;

    public static PostSearchRes fromEntity(Post post, Pet pet) {
        return PostSearchRes.builder()
                .id(post.getId())
                .petProfile(pet.getPetProfileUrl())
                .petName(pet.getName())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}

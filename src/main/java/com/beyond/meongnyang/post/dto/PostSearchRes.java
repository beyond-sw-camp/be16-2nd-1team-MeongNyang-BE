package com.beyond.meongnyang.post.dto;

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

    public static PostSearchRes fromEntity(Post post) {
        return PostSearchRes.builder()
                .id(post.getId())
                .petProfile(post.getUser() != null ? /* p.getUser().getProfileImage() */ null : null)
                .petName(post.getUser() != null ? /* p.getUser().getNickname() */ null : null)
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}

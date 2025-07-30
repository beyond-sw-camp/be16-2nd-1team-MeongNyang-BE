package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Media;
import com.beyond.meongnyang.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostListReq {
    private Long id;
    private String title;
    private String userName;

    public static PostListReq fromEntity(Post post){
        return PostListReq.builder()
                .id(post.getId())
                .title(post.getTitle())
                .userName(post.getUser().getName())
                .build();
    }
}

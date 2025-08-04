package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Like;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostLikeRes {
    private Long postId;
    private String userName;
    private String userProfile;

    public Like likeToEntity(Post post, User user){
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }
}

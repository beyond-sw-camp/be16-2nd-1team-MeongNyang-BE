package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Comment;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCommentCreateReq {
    private Long postId;
    private Long userId;
    private String content;

    public Comment toEntity(Post post, User user){
        return Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();
    }
}

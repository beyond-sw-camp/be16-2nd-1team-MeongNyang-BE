package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.common.domain.Bool;
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
    private String content;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(this.content)
                .delYn(Bool.FALSE)
                .build();
    }
}

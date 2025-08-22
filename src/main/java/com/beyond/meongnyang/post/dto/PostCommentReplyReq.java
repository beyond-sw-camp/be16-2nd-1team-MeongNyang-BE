package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.post.entity.Comment;
import com.beyond.meongnyang.post.entity.CommentTag;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCommentReplyReq {
    private String content;
    private Long mentionUserId;

    public Comment ReplyToEntity(User replyUser, Post post) {
        return Comment.builder()
                .user(replyUser)
                .post(post)
                .content(this.content)
                .delYn(Bool.FALSE)
                .build();
    }

    public CommentTag CommentTagToEntity(Comment replyComment, User mentionUser, User replyUser, Comment parentComment) {
        return CommentTag.builder()
                .comment(replyComment)
                .commentUser(mentionUser)
                .replyUser(replyUser)
                .parentComment(parentComment)
                .build();
    }
}

package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.CommentTag;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentReplyRes {
    private Long id;
    private String replyUserName;
    private String mentionUserName;
    private String content;
    private String createdAt;

    public static PostCommentReplyRes fromEntity(CommentTag tag) {
        return PostCommentReplyRes.builder()
                .id(tag.getId())
                .replyUserName(tag.getReplyUser().getName())
                .mentionUserName(tag.getCommentUser().getName())
                .content(tag.getComment().getContent())
                .createdAt(tag.getCreatedAt().toString())
                .build();
    }
}

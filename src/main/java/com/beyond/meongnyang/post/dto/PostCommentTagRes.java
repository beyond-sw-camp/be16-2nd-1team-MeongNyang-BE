package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.CommentTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCommentTagRes {
    private Long id;
    private String commentUserName;
    private String replyUserName;
    private String content;
    private String createdAt;

    public static PostCommentTagRes fromEntity(CommentTag tag) {
        return PostCommentTagRes.builder()
                .id(tag.getId())
                .commentUserName(tag.getCommentUser().getName())
                .replyUserName(tag.getReplyUser().getName())
                .content(tag.getComment().getContent())
                .createdAt(tag.getCreatedAt().toString())
                .build();
    }
}

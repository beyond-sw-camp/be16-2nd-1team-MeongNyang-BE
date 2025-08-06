package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCommentListRes {
    private Long commentId;
    private String userName;
    private String content;
    private String createdAt;
    private List<PostCommentReplyRes> replies;

    public static PostCommentListRes fromEntity(Comment comment, List<PostCommentReplyRes> replies) {
        return PostCommentListRes.builder()
                .commentId(comment.getId())
                .userName(comment.getUser().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .replies(replies)
                .build();
    }
}

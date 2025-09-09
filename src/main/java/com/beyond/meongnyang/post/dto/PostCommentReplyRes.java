package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.CommentTag;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentReplyRes {
    private Long id;  // 답글의 실제 ID
    private Long userId;
    private Long     mentionUserId;
    private String replyUserName;
    private String mentionUserName;
    private String profileImage;
    private String content;
    private String createdAt;

    public static PostCommentReplyRes fromEntity(CommentTag tag) {
        return PostCommentReplyRes.builder()
                .id(tag.getComment().getId())  // CommentTag의 ID가 아닌 Comment의 ID를 반환
                .userId(tag.getReplyUser().getId())
                .mentionUserId(tag.getCommentUser().getId())
                .replyUserName(tag.getReplyUser().getName())
                .mentionUserName(tag.getCommentUser().getName())
                .profileImage(tag.getReplyUser().getMainPet() == null ? "" : tag.getReplyUser().getMainPet().getPetProfileUrl())
                .content(tag.getComment().getContent())
                .createdAt(tag.getCreatedAt().toString())
                .build();
    }
}
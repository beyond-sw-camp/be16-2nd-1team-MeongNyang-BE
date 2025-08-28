package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Comment;
import com.beyond.meongnyang.post.entity.CommentTag;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCommentListRes {
    private Long commentId;
    private Long userId;
    private String profileImage;
    private String userName;
    private String content;
    private String createdAt;
    private List<PostCommentReplyRes> replies;

    public static PostCommentListRes fromEntity(Comment comment, List<PostCommentReplyRes> replies) {
        User user = comment.getUser();

        return PostCommentListRes.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .profileImage(user.getMainPet() == null ? "" : user.getMainPet().getPetProfileUrl())
                .userName(user.getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .replies(replies)
                .build();
    }
}

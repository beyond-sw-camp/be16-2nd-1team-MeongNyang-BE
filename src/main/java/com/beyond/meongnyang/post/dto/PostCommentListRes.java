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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostCommentListRes {
    private Long commentId;
    private Long userId;
    private String profileImage;
    private String petName;
    private String content;
    private String createdAt;
    private List<PostCommentReplyRes> replies;

    public static PostCommentListRes fromEntity(Comment comment, List<PostCommentReplyRes> replies) {
        User user = comment.getUser();
        String profileImage = "";
        String petName = "";

        Pet pet = user.getPets().stream()
                .filter(p -> p.getId().equals(user.getMainPetId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("펫을 찾을 수 없습니다."));
        profileImage = pet.getPetProfileUrl();
        petName = pet.getName();
        if(petName != null && petName.isEmpty()){
            petName = user.getName();
        }
        return PostCommentListRes.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .profileImage(profileImage)
                .petName(petName)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt().toString())
                .replies(replies)
                .build();
    }
}

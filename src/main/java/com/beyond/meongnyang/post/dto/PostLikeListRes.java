package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Like;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostLikeListRes {
    private Long userId;
    private String profileImage;
    private String userName;
    private String date;

    public static PostLikeListRes fromEntity(Like like){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h:mm");
        User user = like.getUser();
        return PostLikeListRes.builder()
                .userId(user.getId())
                .profileImage(user.getMainPet() == null ? "" : user.getMainPet().getPetProfileUrl())
                .userName(user.getName())
                .date(like.getCreatedAt().format(formatter))
                .build();
    }
}

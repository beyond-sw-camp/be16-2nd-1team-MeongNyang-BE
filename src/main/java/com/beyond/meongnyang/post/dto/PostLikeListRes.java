package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostLikeListRes {
//    private String userProfile;
    private String userName;
    private String date;

    public static PostLikeListRes fromEntity(Post post, User user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 hh:mm");
        return PostLikeListRes.builder()
//                .userProfile(user)
                .userName(user.getNickname())
                .date(post.getCreatedAt().format(formatter))
                .build();
    }
}

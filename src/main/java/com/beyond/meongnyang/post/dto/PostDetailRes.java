package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Like;
import com.beyond.meongnyang.post.entity.Media;
import com.beyond.meongnyang.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailRes {
    private Long id;
    private String userName;
    private String title;
    private String content;
    private int likeCount;
    private List<String> hashTagList;
    private List<String> mediaList;
    private String localDateTime;

    public static PostDetailRes fromEntity(Post post, int likeCount){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        return PostDetailRes.builder()
                .id(post.getId())
                .userName(post.getUser().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(likeCount)
                .hashTagList(
                        post.getHashtags().stream()
                                .map(ht -> ht.getTag().getName()) // Tag → name
                                .toList()
                )
                .mediaList(
                        post.getMediaList().stream()
                            .map(Media::getUrl)
                            .toList()
                )
                .localDateTime(post.getCreatedAt().format(formatter))
                .build();
    }
}

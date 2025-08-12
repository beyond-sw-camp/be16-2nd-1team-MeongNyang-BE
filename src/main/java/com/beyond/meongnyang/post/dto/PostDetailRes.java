package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
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
    private String petName;
    private String petImage;
    private String title;
    private String content;
    private int likeCount;
    private List<String> hashTagList;
    private List<String> mediaList;
    private String date;

    public static PostDetailRes fromEntity(Post post, Pet pet, int likeCount){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        return PostDetailRes.builder()
                .id(post.getId())
                .petName(pet.getName())
                .petImage(pet.getPetProfileUrl())
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
                .date(post.getCreatedAt().format(formatter))
                .build();
    }
}

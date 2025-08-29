package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostSearchRes {
    private Long id;
    private List<String> hashTagList;
    private String thumbnail;

    public static PostSearchRes fromEntity(Post post){
        return PostSearchRes.builder()
                .id(post.getId())
                .hashTagList(
                        post.getHashtags().stream()
                                .map(ht -> ht.getTag().getName()) // Tag → name
                                .toList()
                )
                .thumbnail(
                        post.getMediaList().get(0).getUrl())
                .build();
    }
}

package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.HashTag;
import com.beyond.meongnyang.post.entity.Media;
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
public class PostListReq {
    private Long id;
    private List<String> hashTagList;
    private String thumbnail;

    public static PostListReq fromEntity(Post post){
        return PostListReq.builder()
                .id(post.getId())
                .hashTagList(
                        post.getHashtags().stream()
                                .map(ht -> ht.getTag().getName()) // Tag → name
                                .toList()
                )
                .thumbnail(
                        post.getMediaList().isEmpty() ? null : post.getMediaList().get(0).getUrl())
                .build();
    }
}

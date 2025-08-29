package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Tag;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HashtagRankRes {
    private Long tagId;
    private String name;
    private Long count;

    public static HashtagRankRes fromEntity(Tag tag, Long count) {
        return HashtagRankRes.builder()
                .tagId(tag.getId())
                .name(tag.getName())
                .count(count)
                .build();
    }
}

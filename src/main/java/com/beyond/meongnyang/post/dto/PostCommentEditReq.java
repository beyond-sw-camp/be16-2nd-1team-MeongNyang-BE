package com.beyond.meongnyang.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCommentEditReq {
    private Long postId;
    private Long commentId;
}

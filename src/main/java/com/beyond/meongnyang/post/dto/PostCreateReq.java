package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.post.entity.Post;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostCreateReq {
    @NotEmpty(message = "내용을 입력해주세요.")
    @Size(max = 1000,message = "내용은 최대 1000자까지 입력 가능합니다.")
    private String content;

    public Post postToEntity(){
        return Post.builder()
                .content(this.content)
                .build();
    }
}

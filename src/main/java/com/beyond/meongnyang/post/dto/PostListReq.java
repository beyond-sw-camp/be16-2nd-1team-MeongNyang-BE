package com.beyond.meongnyang.post.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.post.entity.HashTag;
import com.beyond.meongnyang.post.entity.Media;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
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
//    private String petName;
    private List<String> hashTagList;
    private String thumbnail;

    public static PostListReq fromEntity(Post post){
        String petName = "";
        User user = post.getUser();
        Pet pet = user.getPets().stream()
                .filter(p -> p.getId().equals(user.getMainPetId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("펫을 찾을 수 없습니다."));
        petName = pet.getName();
        if(petName != null && petName.isEmpty()){
            petName = user.getName();
        }
        return PostListReq.builder()
            .id(post.getId())
//            .petName(petName)
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

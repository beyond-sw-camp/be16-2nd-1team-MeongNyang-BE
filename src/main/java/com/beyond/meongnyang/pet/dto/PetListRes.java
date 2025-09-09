package com.beyond.meongnyang.pet.dto;

import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class PetListRes {
    private String userName;
    private List<PetDetailRes> pets;

    public static PetListRes fromEntity (User user, List<Pet> petList) {
        return PetListRes.builder()
                .userName(user.getName())
                .pets(petList.stream().map(pet -> PetDetailRes.fromEntity(pet)).toList())
                .build();
    }
}

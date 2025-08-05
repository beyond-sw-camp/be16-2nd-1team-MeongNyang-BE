package com.beyond.meongnyang.pet.dto;

import com.beyond.meongnyang.pet.entity.Gender;
import com.beyond.meongnyang.pet.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

public class PetDetailRes {
    private String petOrder;
    private String species;
    private String name;
    private Integer age;
    private Gender gender;
    private BigDecimal weight;
    private String petProfile;
    private LocalDate birthday;

    public static PetDetailRes fromEntity(Pet pet) {
        return PetDetailRes.builder()
                .petOrder(pet.getSpecies().getPetOrder())
                .species(pet.getSpecies().getSpecies())
                .name(pet.getName())
                .age(pet.getAge())
                .gender(pet.getGender())
                .weight(pet.getWeight())
                .petProfile(pet.getPetProfile())
                .birthday(pet.getBirthday())
                .build();
    }
}

package com.beyond.meongnyang.pet.dto;

import com.beyond.meongnyang.pet.entity.Gender;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.species.entity.Species;
import com.beyond.meongnyang.user.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class PetRegisterReq {
    @NotEmpty(message = "반려동물 이름을 입력해주세요")
    private String name;
    @NotNull(message = "나이를 입력해주세요")
    private Integer age;
    @NotNull(message = "성별을 입력해주세요")
    private Gender gender;
    @NotNull(message = "몸무게를 입력해주세요")
    @DecimalMin(value = "0.01", message = "몸무게는 0보다 커야 합니다") // TODO: 프론트에서 소수점 2자리 제약 걸어주기
    private BigDecimal weight;
    private String url;
    private LocalDate birthday;
    private String introduce;

    // 프론트에서 선택한 speciesId가 넘어옴.
    private Long speciesId;

    public Pet toEntity(User user, Species species) {
        return Pet.builder()
                .name(this.name)
                .age(this.age)
                .gender(this.gender)
                .weight(this.weight)
                .petProfileUrl(this.url)
                .birthday(this.birthday)
                .introduce(this.introduce)
                .user(user)
                .species(species)
                .build();
    }
}

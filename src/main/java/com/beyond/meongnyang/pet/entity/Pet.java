package com.beyond.meongnyang.pet.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.pet.dto.PetRegisterReq;
import com.beyond.meongnyang.species.entity.Species;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

@Table(name = "pet")
public class Pet extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    // TODO: 공개 여부는 후에


    @Column(name = "pet_profile", nullable = true, length = 255)
    private String petProfileUrl;

    // TODO: 1년 뒤에 떡국 먹게 하기
    @Column(name = "birthday", nullable = true)
    private LocalDate birthday;

    // pet 등록 관련 소프트 딜리트
    @Column(name = "is_del", nullable = false)
    @Builder.Default
    private String isDel = "N";


    /* ******************** 연관관계 ******************* */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private Species species;

    /* ******************** 매서드 ********************/
    // pet 인적 사항 업데이트
    public void updatePet(PetRegisterReq req, Species species) {
        this.name = req.getName();
        this.age = req.getAge();
        this.gender = req.getGender();
        this.weight = req.getWeight();
        this.birthday = req.getBirthday();
        this.petProfileUrl = req.getUrl();
        this.species = species;

    }

    public void delPet() {
        this.isDel = "Y";
    }
}

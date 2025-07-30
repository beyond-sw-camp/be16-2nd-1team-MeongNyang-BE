package com.beyond.meongnyang.pet.Entity;

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
public class Pet {
    @Id
    @GeneratedValue()
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    @Column(name = "gender", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal weight;

    // TODO: 공개 여부는 후에

    @Column(name = "pet_profile", nullable = true, length = 255)
    private String petProfile;

    // TODO: 1년 뒤에 떡국 먹게 하기
    @Column(name = "birthday", nullable = true)
    private LocalDate birthday;



    /* ******************** 연관관계 ******************* */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private Species species;

    /* ******************** 매서드 ********************/
    public void updateImgUrl (String url) {
        this.petProfile = url;
    }
}

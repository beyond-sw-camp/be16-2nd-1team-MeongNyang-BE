package com.beyond.meongnyang.species.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "species")
public class Species {
    //TODO: 프론트에서 데이터 가지고 있게 하기
    // sql등록 , yml 파일 수정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_order", nullable = false)
    private String petOrder; // 대분류

    @Column(name = "species", nullable = false)
    private String species; //소분류

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private Size size; // 대, 중, 소
}

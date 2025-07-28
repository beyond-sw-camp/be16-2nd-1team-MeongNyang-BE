package com.beyond.meongnyang.user.domain.Pet;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order", nullable = false)
    private String order; // 대분류

    @Column(name = "species", nullable = false)
    private String species;

    @Enumerated(EnumType.STRING)
    @Column(name = " size", nullable = false)
    private Size size; // 대, 중, 소
}

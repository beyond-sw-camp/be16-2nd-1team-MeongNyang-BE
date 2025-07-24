package com.beyond.meongnyang.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

public class Pet {
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    // TODO: 입력시 설정되게 이넘타입?
    @Column(name = "sex", nullable = false, length = 255)
    private String sex;

}

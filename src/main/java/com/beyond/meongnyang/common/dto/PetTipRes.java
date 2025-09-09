package com.beyond.meongnyang.common.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PetTipRes {
    private String weather;
    private String temperature;
    private String petType;
}

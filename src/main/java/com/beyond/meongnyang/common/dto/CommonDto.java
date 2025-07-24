package com.beyond.meongnyang.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommonDto {
    private Object result;
    private int stauts_code;
    private String stauts_message;
}

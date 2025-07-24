package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFindDto {
    String name;
    String email;
    String phone;

}

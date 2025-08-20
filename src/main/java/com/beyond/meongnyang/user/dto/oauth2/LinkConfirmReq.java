package com.beyond.meongnyang.user.dto.oauth2;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LinkConfirmReq {
    @NotBlank
    private String linkTicket;
}

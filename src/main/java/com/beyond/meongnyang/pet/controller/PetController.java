package com.beyond.meongnyang.pet.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.pet.dto.PetRegisterReq;
import com.beyond.meongnyang.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PetRegisterReq req) {
        this.petService.register(req);
        return new ResponseEntity<>(CommonRes.ofSuccess(("당신의 반려동물이 등록되었습니다."),
                HttpStatus.CREATED.value(), "Pet 등록 완료"), HttpStatus.CREATED);
    }
}

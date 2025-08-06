package com.beyond.meongnyang.pet.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.pet.dto.PetRegisterReq;
import com.beyond.meongnyang.pet.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestPart("PetRegisterReq") PetRegisterReq req, @RequestPart(value = "url", required = false) MultipartFile petImg) {
        this.petService.register(req, petImg);
        return new ResponseEntity<>(CommonRes.ofSuccess(("당신의 반려동물이 등록되었습니다."),
                HttpStatus.CREATED.value(), "Pet 등록 완료"), HttpStatus.CREATED);
    }

    // 유저가 등록한 애완동물 목록
    @GetMapping("/list")
    public ResponseEntity<?> findByUser() {
        return new ResponseEntity<>(CommonRes.ofSuccess(petService.findByUser(), HttpStatus.OK.value(), "Pet 목록 조회 완료"), HttpStatus.OK);
    }


    // 유저가 등록한 펫 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @Valid @RequestPart("PetRegisterReq") PetRegisterReq req, @RequestPart(value = "url", required = false) MultipartFile petImg) {
        this.petService.updatePet(id, req, petImg);
        return new ResponseEntity<>(CommonRes.ofSuccess("애완동물 수정이 완료됐습니다.",
                HttpStatus.OK.value(), "애완동물 수정 완료"), HttpStatus.OK);
    }

    // 유저가 등록한 펫 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Long id) {
        String petName = this.petService.deletPet(id);
        return new ResponseEntity<>(CommonRes.ofSuccess("당신의 " + petName +"이 삭제되었습니다.", HttpStatus.OK.value(), "pet 삭제 완료"), HttpStatus.OK);
    }
}

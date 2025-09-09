package com.beyond.meongnyang.common.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.common.dto.PetTipRes;
import com.beyond.meongnyang.common.service.PerplexityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pplx")
public class PerplexityController {

    private final PerplexityService perplexityService;

    @PostMapping("/pet-tip")
    public ResponseEntity<?> getPetTip(@RequestBody PetTipRes petTipRes) {
        return ResponseEntity.ok(CommonRes.ofSuccess(perplexityService.getPetTip(petTipRes), HttpStatus.OK.value(), "AI 전문가 응답 완료"));
    }
}

package com.beyond.meongnyang.species.controller;

import com.beyond.meongnyang.species.dto.SpeciesListRes;
import com.beyond.meongnyang.species.dto.SpeciesSearchReq;
import com.beyond.meongnyang.species.service.SpeciesService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/species")
public class SpeciesController {
    private final SpeciesService speciesService;

    // TODO: front 화면에서 유저가 종을 선택 -> id를 백엔드로 가져오기
    // 굳이
    @GetMapping("/list")
    public ResponseEntity<?> findAll() {
        List<SpeciesListRes> speciesList = this.speciesService.findAll();
        return new ResponseEntity<> (CommonRes.ofSuccess(speciesList, HttpStatus.OK.value(), "Species 목록 조회 완료"), HttpStatus.OK);

    }

    // 검색 api
    // Size enum타입 유의해서 선택
    @GetMapping("/search")
    public ResponseEntity<?> search(@ModelAttribute SpeciesSearchReq speciesSearchReq) {
        List <SpeciesListRes> speciesList = this.speciesService.findAll(speciesSearchReq);
        return new ResponseEntity<> (CommonRes.ofSuccess(speciesList, HttpStatus.OK.value(), "Species 검색 조회 완료"), HttpStatus.OK);

    }


}

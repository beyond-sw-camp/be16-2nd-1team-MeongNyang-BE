package com.beyond.meongnyang.market.controller;

import com.beyond.meongnyang.common.dto.ResponseDto;
import com.beyond.meongnyang.market.dto.MarketPostCreateReq;
import com.beyond.meongnyang.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @PostMapping("/posts")
    public ResponseEntity<?> createMarketPost (@RequestPart(name = "post") MarketPostCreateReq marketPostCreateReq,
                                               @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        Long createdPostId = marketService.createMarketPost(marketPostCreateReq, imageFiles);
        return new ResponseEntity<>(
                ResponseDto.ofSuccess(createdPostId, HttpStatus.CREATED.value(), "거래글이 작성되었습니다."),
                HttpStatus.CREATED
        );
    }
}

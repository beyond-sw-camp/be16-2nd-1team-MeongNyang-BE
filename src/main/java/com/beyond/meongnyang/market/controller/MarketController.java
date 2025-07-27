package com.beyond.meongnyang.market.controller;

import com.beyond.meongnyang.common.dto.ResponseDto;
import com.beyond.meongnyang.market.dto.MarketPostCreateRequest;
import com.beyond.meongnyang.market.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;

    @PostMapping("/posts")
    public ResponseEntity<?> createMarektPost
            (@RequestPart(name = "post") MarketPostCreateRequest marketPostCreateRequest,
             @RequestPart(value = "imageFiles", required = false)List<MultipartFile> imageFiles) {
        Long createdPostId = marketService.createMarketPost(marketPostCreateRequest, imageFiles);
        return new ResponseEntity<>(
                ResponseDto.ofSuccess(createdPostId, HttpStatus.CREATED.value(), "post is created"),
                HttpStatus.CREATED
        );
    }
}

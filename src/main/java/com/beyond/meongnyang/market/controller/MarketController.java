package com.beyond.meongnyang.market.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.market.dto.MarketPostCreateReq;
import com.beyond.meongnyang.market.dto.MarketPostUpdateReq;
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

    // 거래글 등록
    @PostMapping("/posts")
    public ResponseEntity<?> marketPostCreate (@RequestPart(name = "post") MarketPostCreateReq marketPostCreateReq,
                                               @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        Long postId = marketService.marketPostCreate(marketPostCreateReq, imageFiles);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postId,
                        HttpStatus.CREATED.value(),
                        "거래글이 작성되었습니다."),
                HttpStatus.CREATED
        );
    }

    // 거래글 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> marketPostUpdate(@PathVariable("id") Long id,
                                              @RequestPart(name = "post") MarketPostUpdateReq marketPostUpdateReq,
                                              @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        marketService.marketPostUpdate(id, marketPostUpdateReq, imageFiles);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "거래글을 수정했습니다."
                ), HttpStatus.OK
        );
    }

    // 거래글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> marketPostDelete(@PathVariable("id") Long id) {
        marketService.marketPostDelete(id);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "거래글을 삭제했습니다."
                ), HttpStatus.OK
        );
    }
}

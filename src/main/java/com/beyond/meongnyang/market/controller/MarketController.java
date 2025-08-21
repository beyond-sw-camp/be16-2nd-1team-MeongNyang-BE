package com.beyond.meongnyang.market.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.market.dto.MarketPostCreateReq;
import com.beyond.meongnyang.market.dto.MarketPostListReq;
import com.beyond.meongnyang.market.dto.MarketPostUpdateReq;
import com.beyond.meongnyang.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> createMarketPost (@RequestPart(name = "post") MarketPostCreateReq marketPostCreateReq,
                                               @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        Long postId = marketService.createMarketPost(marketPostCreateReq, imageFiles);
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
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> updateMarketPost(@PathVariable("id") Long id,
                                              @RequestPart(name = "post") MarketPostUpdateReq marketPostUpdateReq,
                                              @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        marketService.updateMarketPost(id, marketPostUpdateReq, imageFiles);
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
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> deleteMarketPost(@PathVariable("id") Long id) {
        marketService.deleteMarketPost(id);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "거래글을 삭제했습니다."
                ), HttpStatus.OK
        );
    }

    // 거래글 목록조회
    @GetMapping("/posts")
    public ResponseEntity<?> marketPostList(@PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.findAllVisible(pageable),
                        HttpStatus.OK.value(),
                        "거래글 목록 조회에 성공했습니다."
                ), HttpStatus.OK
        );
    }

    // 거래글 상세조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<?> marketPostDetail(@PathVariable("id") Long id) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.marketPostDetail(id),
                        HttpStatus.OK.value(),
                        "거래글 상세 조회에 성공했습니다."
                ), HttpStatus.OK
        );
    }

    // 구매목록 조회
    @GetMapping("/purchases")
    public ResponseEntity<?> findPurchases(@PageableDefault(value = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.findPurchases(pageable),
                        HttpStatus.OK.value(),
                        "구매목록 조회에 성공했습니다."
                ), HttpStatus.OK
        );
    }

    // 판매목록 조회
    @GetMapping("/sales")
    public ResponseEntity<?> findSales(@PageableDefault(value = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.findSales(pageable),
                        HttpStatus.OK.value(),
                        "판매목록 조회에 성공했습니다."
                ), HttpStatus.OK
        );
    }

//    찜하기
    @PostMapping("/{id}/like")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> likeMarketPost(@PathVariable("id") Long postId) {
        Long wishListId = marketService.likeMarketPost(postId);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        wishListId,
                        HttpStatus.CREATED.value(),
                        "찜목록에 추가되었습니다."),
                HttpStatus.CREATED
        );
    }

//    찜 취소
    @DeleteMapping("/{id}/like")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> unlikeMarketPost(@PathVariable("id") Long postId) {
        marketService.unlikeMarketPost(postId);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postId,
                        HttpStatus.OK.value(),
                        "찜목록에서 삭제되었습니다."),
                HttpStatus.OK
        );
    }

//    찜 목록조회
    @GetMapping("/like")
    public ResponseEntity<?> findWishlist(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MarketPostListReq> page = marketService.findWishlist(pageable);
        return ResponseEntity.ok(page);
    }
}

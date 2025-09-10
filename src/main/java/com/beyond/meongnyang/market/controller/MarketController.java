package com.beyond.meongnyang.market.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.market.dto.*;
import com.beyond.meongnyang.market.entity.Category;
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
    public ResponseEntity<?> marketPostList(@RequestParam(required = false) Category category,
                                            @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        marketService.findAllVisible(category, pageable),
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

    // 찜하기
    @PostMapping("/{id}/likes")
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

    // 찜 취소
    @DeleteMapping("/{id}/likes")
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

    // 찜 목록조회
    @GetMapping("/likes")
    public ResponseEntity<?> findWishlist(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MarketPostListRes> page = marketService.findWishlist(pageable);
        return ResponseEntity.ok(page);
    }

    // 금액 임시 저장
    @PostMapping("/payments/saveAmount")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> saveAmount(@RequestBody SaveAmountReq req) {
        marketService.saveAmount(req);
        return new ResponseEntity<>(
            CommonRes.ofSuccess(
                    null,
                    HttpStatus.OK.value(),
                    "금액 임시저장에 성공했습니다."),
            HttpStatus.OK
        );
    }

    // 결제 금액 검증
    @PostMapping("/payments/verifyAmount")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> verifyAmount(@RequestBody SaveAmountReq req) {
        marketService.verifyAmount(req);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        null,
                        HttpStatus.OK.value(),
                        "금액 검증에 성공했습니다."
                ),
                HttpStatus.OK
        );
    }

    // 결제 승인
    @PostMapping("/payments/confirm")
    @PreAuthorize("@securityCheck.checkUserAccess()")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmReq req) {
        PaymentConfirmRes res = marketService.confirmPayment(req);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        res,
                        HttpStatus.OK.value(),
                        "결제 승인 완료했습니다."),
                HttpStatus.OK
        );
    }
}

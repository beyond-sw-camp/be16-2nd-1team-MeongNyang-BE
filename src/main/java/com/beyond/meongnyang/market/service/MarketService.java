package com.beyond.meongnyang.market.service;

import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.market.dto.MarketPostCreateReq;
import com.beyond.meongnyang.market.dto.MarketPostDetailRes;
import com.beyond.meongnyang.market.dto.MarketPostListReq;
import com.beyond.meongnyang.market.dto.MarketPostUpdateReq;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.ProductImage;
import com.beyond.meongnyang.market.repository.MarketPostRepository;
import com.beyond.meongnyang.market.repository.ProductImageRepository;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class MarketService {
    private final MarketPostRepository marketPostRepository;
    private final ProductImageRepository productImageRepository;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

//    거래글 생성
    public Long marketPostCreate(MarketPostCreateReq marketPostCreateReq,
                                 List<MultipartFile> imageFiles) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 찾아오기
        MarketPost marketPost = marketPostCreateReq.toEntity();

//        3. seller 컬럼 저장
        marketPost.setSeller(user);

//        4. 이미지 컬럼 저장
        if(imageFiles != null && !imageFiles.isEmpty()){
//            이미지 업로드 - s3
            List<String> urls = s3UploadService.upload(imageFiles);
//            이미지 업로드 - db
            Integer mainImageIndex = marketPostCreateReq.getMainImageIndex();
            if (mainImageIndex < 0 || mainImageIndex >= urls.size()) {
                throw new IllegalArgumentException("대표 이미지 인덱스가 유효하지 않습니다.");
            }
            marketPost.setThumbnailImage(urls.get(mainImageIndex));

            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .marketPost(marketPost)
                        .imageUrl(url)
                        .build();
                marketPost.addProductImage(productImage);
            }
        }
//        5. 거래글 저장 및 id 리턴
        return marketPostRepository.save(marketPost).getId();
    }

//    거래글 수정
    public Long marketPostUpdate(Long id,
                                 MarketPostUpdateReq marketPostUpdateReq,
                                 List<MultipartFile> imageFiles) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 찾아오기
        MarketPost marketPost =  marketPostRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 거래글입니다."));

//        작성자 확인
        if (!Objects.equals(user.getId(), marketPost.getSeller().getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

//        3. 텍스트 정보 업데이트
        marketPost.updateMarketPost(marketPostUpdateReq);

//        4. 이미지 업데이트
        if(imageFiles != null && !imageFiles.isEmpty()) {
//            기존 이미지 S3 삭제
            List<ProductImage> productImageList = marketPost.getProductImageList();
            for (ProductImage productImage : productImageList) {
                String imageUrl = productImage.getImageUrl();
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                s3UploadService.delete(fileName);
            }
//            기존 이미지 db 삭제 (db에서만 삭제하면 영속성컨텍스트로 다시 자바객체에 남아있던 이미지를 db에 업데이트함)
            marketPost.getProductImageList().clear();
//            이미지 재 업로드
            List<String> urls = s3UploadService.upload(imageFiles);
//            대표이미지 사용자 선택
            Integer mainImageIndex = marketPostUpdateReq.getMainImageIndex();
            if (mainImageIndex < 0 || mainImageIndex >= urls.size()) {
                throw new IllegalArgumentException("대표 이미지 인덱스가 유효하지 않습니다.");
            }
            marketPost.setThumbnailImage(urls.get(mainImageIndex));
            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .marketPost(marketPost)
                        .imageUrl(url)
                        .build();
                productImageRepository.save(productImage);
            }
        }
        return marketPost.getId();
    }

//    거래글 삭제
    public void marketPostDelete(Long id) throws AccessDeniedException {
//        1. 로그인한 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 찾아오기
        MarketPost marketPost =  marketPostRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 거래글입니다."));

        // 작성자 확인 (로그인한 사용자, 거래글 작성자)
        if (!Objects.equals(user.getId(), marketPost.getSeller().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }
        marketPost.deleteMarketPost("Y");
    }

//    거래글 목록조회
    public Page<MarketPostListReq> marketPostList(Pageable pageable) {
//        1. pageable(page, size 정보)대로 marketPost를 list로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAll(pageable);
//        2. list에서 marketPost를 하나씩 꺼내서 dto로 변환
        return marketPostList.map(p-> MarketPostListReq.fromEntity(p));
    }

//    거래글 상세조회
    public MarketPostDetailRes marketPostDetail(Long id) {
        MarketPost marketPost = marketPostRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 거래글입니다."));
        return MarketPostDetailRes.fromEntity(marketPost);
    }

//    TODO : 결제기능 구현 후에 buyer 세팅 가능
//    구매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> getPurchases(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 객체를 구매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllByBuyer_Id(user.getId(), pageable);

//        3. 각 거래글 객체를 dto로 변환해서 반환
        return marketPostList.map(MarketPostListReq::fromEntity);
    }

//    판매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> getSales(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 객체를 판매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllBySeller_Id(user.getId(), pageable);

//        3. 각 거래글 객체를 dto로 변환해서 반환
        return marketPostList.map(MarketPostListReq::fromEntity);
    }
}

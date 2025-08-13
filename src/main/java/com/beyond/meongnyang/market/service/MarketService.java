package com.beyond.meongnyang.market.service;

import com.beyond.meongnyang.admin.repository.ReportRepository;
import com.beyond.meongnyang.common.CommonService;
import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.market.dto.*;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.ProductImage;
import com.beyond.meongnyang.market.entity.Wishlist;
import com.beyond.meongnyang.market.repository.MarketPostRepository;
import com.beyond.meongnyang.market.repository.ProductImageRepository;
import com.beyond.meongnyang.market.repository.WishlistRepository;
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
    private final CommonService commonService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final WishlistRepository wishlistRepository;

//    거래글 생성
    public Long createMarketPost(MarketPostCreateReq marketPostCreateReq,
                                 List<MultipartFile> imageFiles) {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

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
    public Long updateMarketPost(Long id,
                                 MarketPostUpdateReq marketPostUpdateReq,
                                 List<MultipartFile> imageFiles) {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();
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
    public void deleteMarketPost(Long id) throws AccessDeniedException {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

//        2. 거래글 찾아오기
        MarketPost marketPost =  marketPostRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 거래글입니다."));

        // 작성자 확인 (로그인한 사용자, 거래글 작성자)
        if (!Objects.equals(user.getId(), marketPost.getSeller().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }
        marketPost.deleteMarketPost("Y");
    }

//    거래글 목록조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> marketPostList(Pageable pageable) {
//        1. pageable(page, size 정보)대로 marketPost를 list로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAll(pageable);
//        2. list에서 marketPost를 하나씩 꺼내서 dto로 변환 (+ 찜개수)
        return marketPostList.map(post ->
                MarketPostListReq.fromEntity(post, wishlistRepository.countByMarketPost(post))
        );
    }

//    거래글 상세조회
    @Transactional(readOnly = true)
    public MarketPostDetailRes marketPostDetail(Long id) {
        MarketPost marketPost = marketPostRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 거래글입니다."));
        return MarketPostDetailRes.fromEntity(marketPost);
    }

//    TODO : 결제기능 구현 후에 buyer 세팅 가능
//    구매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> findPurchases(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 객체를 구매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllByBuyerId(user.getId(), pageable);

//        3. 각 거래글 객체를 dto로 변환해서 반환 (+ 찜개수)
        return marketPostList.map(post ->
                MarketPostListReq.fromEntity(post, wishlistRepository.countByMarketPost(post))
        );
    }

//    판매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> findSales(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 거래글 객체를 판매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllBySellerId(user.getId(), pageable);

//        3. 각 거래글 객체를 dto로 변환해서 반환 (+ 찜개수)
        return marketPostList.map(post ->
                MarketPostListReq.fromEntity(post, wishlistRepository.countByMarketPost(post))
        );
    }

//    찜하기
    public Long likeMarketPost(Long postId) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 마켓포스트 객체 가져오기
        MarketPost marketPost = marketPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

//        3. 중복 찜 여부 확인하기
        boolean alreadLiked = wishlistRepository.findByUserAndMarketPost(user, marketPost).isPresent();
        if(alreadLiked) {
            throw new IllegalStateException("이미 찜한 거래글입니다.");
        }

//        4. wishlist 객체 조립 및 생성
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .marketPost(marketPost)
                .build();

//        5. save 및 wishlist 리턴
        wishlistRepository.save(wishlist);
        return wishlist.getId();
    }

//    찜 취소
    public void unlikeMarketPost(Long id) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

//        2. 찜한 마켓포스트 객체 가져오기
        MarketPost marketPost = marketPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

//        3. 찜목록에서 삭제하기
        wishlistRepository.deleteByUserAndMarketPost(user, marketPost);
        wishlistRepository.flush();
    }

//    찜목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListReq> findWishlist(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

        // 2. 해당 사용자의 찜(Wishlist) 페이지 조회
        Page<Wishlist> wishlistPage = wishlistRepository.findAllByUser(user, pageable);

        // 3. 각 Wishlist → MarketPost 꺼내서 DTO 변환 + 전체 찜 개수 포함
        return wishlistPage.map(w -> {
            MarketPost post = w.getMarketPost();
            return MarketPostListReq.fromEntity(post, wishlistRepository.countByMarketPost(post));
        });
    }

    // 거래글 신고하기
    public void reportMarketPost(Long marketPostId, MarketReportCreateReq marketReportCreateReq) {
        User reportUser = commonService.getCurrentUser();
        MarketPost marketPost = marketPostRepository.findById(marketPostId).orElseThrow(()-> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
        reportRepository.save(marketReportCreateReq.ReportToEntity(marketPost, reportUser));
    }
}

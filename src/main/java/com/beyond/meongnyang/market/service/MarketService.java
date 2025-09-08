package com.beyond.meongnyang.market.service;

import com.beyond.meongnyang.admin.repository.ReportRepository;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.chat.service.ChatRedisService;
import com.beyond.meongnyang.common.customexception.AlreadySoldException;
import com.beyond.meongnyang.common.customexception.AmountMismatchException;
import com.beyond.meongnyang.common.customexception.TossPaymentException;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.common.service.S3UploadService;
import com.beyond.meongnyang.market.dto.*;
import com.beyond.meongnyang.market.entity.*;
import com.beyond.meongnyang.market.repository.*;
import com.beyond.meongnyang.notification.entity.NotificationType;
import com.beyond.meongnyang.notification.service.NotificationService;
import com.beyond.meongnyang.user.entity.Role;
import com.beyond.meongnyang.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarketService {
    private final MarketPostRepository marketPostRepository;
    private final ProductImageRepository productImageRepository;
    private final S3UploadService s3UploadService;
    private final CommonService commonService;
    private final ReportRepository reportRepository;
    private final WishlistRepository wishlistRepository;
    private final TransactionRepository transactionRepository;
    private final PointTransactionRepository pointTransactionRepository;
    @Qualifier("paymentInventory")
    private final RedisTemplate<String, String> paymentRedisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final ChatRedisService chatRedisService;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

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
        if (imageFiles != null && !imageFiles.isEmpty()) {
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
        MarketPost marketPost = marketPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

//        작성자 확인
        if (!Objects.equals(user.getId(), marketPost.getSeller().getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

//        3. 텍스트 정보 업데이트
        marketPost.updateMarketPost(marketPostUpdateReq);

//        4. 이미지 업데이트
        if (imageFiles != null && !imageFiles.isEmpty()) {
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
        MarketPost marketPost = marketPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

        // 작성자 확인 (로그인한 사용자, 거래글 작성자)
        if (!Objects.equals(user.getId(), marketPost.getSeller().getId()) && user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }
        marketPost.deleteMarketPost("Y");
    }

    //    거래글 목록조회
    @Transactional(readOnly = true)
    public Page<MarketPostListRes> findAllVisible(Pageable pageable) {
        // 1. 거래글 목록 조회
        Page<MarketPost> page = marketPostRepository.findAllByDelYn("N", pageable);

        // 2. 로그인한 사용자 조회
        User user = commonService.getCurrentUser();

        // 3. Page<MarketPost> -> Page<MarketPostListRes> 변환
        return page.map(post -> {
            // 찜 개수 조회
            int likeCount = wishlistRepository.countByMarketPost(post);

            // 현재 유저가 찜했는지 여부 확인
            boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, post);

            // DTO 변환
            return MarketPostListRes.fromEntity(post, likeCount, alreadyLiked);
        });
    }

    //    거래글 상세조회
    @Transactional(readOnly = true)
    public MarketPostDetailRes marketPostDetail(Long id) {
        User user = commonService.getCurrentUser();
        MarketPost marketPost = marketPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

        // 찜여부 확인
        boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, marketPost);

        return MarketPostDetailRes.fromEntity(marketPost, alreadyLiked);
    }

    //    구매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListRes> findPurchases(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

//        2. 거래글 객체를 구매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllByBuyerId(user.getId(), pageable);

//        3. Page<MarketPost> -> Page<MarketPostListRes> 변환
        return marketPostList.map(post -> {
            // 찜 개수 조회
            int likeCount = wishlistRepository.countByMarketPost(post);

            // 현재 유저가 찜했는지 여부 확인
            boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, post);

            // DTO 변환
            return MarketPostListRes.fromEntity(post, likeCount, alreadyLiked);
        });
    }

    //    판매목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListRes> findSales(Pageable pageable) {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

//        2. 거래글 객체를 판매자id로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAllBySellerId(user.getId(), pageable);

//        3. Page<MarketPost> -> Page<MarketPostListRes> 변환
        return marketPostList.map(post -> {
            // 찜 개수 조회
            int likeCount = wishlistRepository.countByMarketPost(post);

            // 현재 유저가 찜했는지 여부 확인
            boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, post);

            // DTO 변환
            return MarketPostListRes.fromEntity(post, likeCount, alreadyLiked);
        });
    }

    //    찜하기
    public Long likeMarketPost(Long postId) {
//        1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

//        2. 마켓포스트 객체 가져오기
        MarketPost marketPost = marketPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

//        3. 중복 찜 여부 확인하기
        boolean alreadLiked = wishlistRepository.existsByUserAndMarketPost(user, marketPost);
        if (alreadLiked) {
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
        User user = commonService.getCurrentUser();

//        2. 찜한 마켓포스트 객체 가져오기
        MarketPost marketPost = marketPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 거래글입니다."));

//        3. 찜목록에서 삭제하기
        wishlistRepository.deleteByUserAndMarketPost(user, marketPost);
        wishlistRepository.flush();
    }

    // 찜목록 조회
    @Transactional(readOnly = true)
    public Page<MarketPostListRes> findWishlist(Pageable pageable) {
        //  1. 로그인한 사용자 정보 가져오기
        User user = commonService.getCurrentUser();

        // 2. 해당 사용자의 찜(Wishlist) 페이지 조회
        Page<Wishlist> wishlistPage = wishlistRepository.findAllByUser(user, pageable);

        // 3. 각 Wishlist → MarketPost 꺼내서 DTO 변환 + 전체 찜 개수 포함
        return wishlistPage.map(w -> {
            MarketPost post = w.getMarketPost();
            // 찜 개수 조회
            int likeCount = wishlistRepository.countByMarketPost(post);
            // 현재 유저가 찜했는지 여부 확인
            boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, post);
            // DTO 변환
            return MarketPostListRes.fromEntity(post, likeCount, alreadyLiked);
        });
    }

    // 거래글 신고하기
    public void reportMarketPost(Long marketPostId, MarketReportCreateReq marketReportCreateReq) {
        User reportUser = commonService.getCurrentUser();
        MarketPost marketPost = marketPostRepository.findById(marketPostId).orElseThrow(() -> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
        reportRepository.save(marketReportCreateReq.ReportToEntity(marketPost, reportUser));
    }

    // 관리자 (전체 거래글 조회)
    @Transactional(readOnly = true)
    public Page<MarketPostListRes> marketPostList(Pageable pageable) {
//        1. pageable(page, size 정보)대로 marketPost를 list로 가져오기
        Page<MarketPost> marketPostList = marketPostRepository.findAll(pageable);

        User user = commonService.getCurrentUser();

//        2. list에서 marketPost를 하나씩 꺼내서 dto로 변환 (+ 찜개수)
        return marketPostList.map(post -> {
            // 찜 개수 조회
            int likeCount = wishlistRepository.countByMarketPost(post);

            // 현재 유저가 찜했는지 여부 확인
            boolean alreadyLiked = wishlistRepository.existsByUserAndMarketPost(user, post);

            return MarketPostListRes.fromEntity(post, likeCount, alreadyLiked);
        });
    }

    // 금액 임시 저장
    public void saveAmount(SaveAmountReq req) {
        paymentRedisTemplate.opsForValue().set(req.getOrderId(), String.valueOf(req.getAmount()));
    }

    // 결제 금액 검증
    public void verifyAmount(SaveAmountReq req) {
        String savedAmount = paymentRedisTemplate.opsForValue().get(req.getOrderId());

        if (savedAmount == null || !savedAmount.equals(String.valueOf(req.getAmount()))) {
            throw new IllegalStateException("결제 금액이 불일치합니다");
        }

        paymentRedisTemplate.delete(req.getOrderId());
    }

    // 결제 승인
    public PaymentConfirmRes confirmPayment(PaymentConfirmReq req) {
        User buyer = commonService.getCurrentUser();
        String[] orderSplit = req.getOrderId().split("_");
        Long roomId = Long.parseLong(orderSplit[2]);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException(""));
        MarketPost marketPost = chatRoom.getMarketPost();

        // 허가받은 채팅방에 소속되어 있는지 구매 요청을 받았는 지 검증
        if (chatRoom.getChatParticipantList().stream().noneMatch(cp -> cp.getUser().getId().equals(buyer.getId())))
            throw new AccessDeniedException("채팅방에 구매자로 속해 있지 않습니다.");

        if (chatRoom.getIsPurchaseApproved() == Bool.FALSE)
            throw new AccessDeniedException("판매자가 결제요청을 하지 않았습니다.");

        // 이미 판매되었는 지 검증
        if (marketPost.getSaleStatus() == SaleStatus.SOLD) throw new AlreadySoldException("이미 판매된 제품입니다.");

        // 결제 금액 검증
        if (marketPost.getPrice() != req.getAmount()) throw new AmountMismatchException("판매자가 설정한 금액과 다릅니다.");

        // 1. Toss API 요청 헤더 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(tossSecretKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Toss API 요청 바디 준비
        HttpEntity<PaymentConfirmReq> entity = new HttpEntity<>(req, headers);

        // 3. Toss 결제 승인 API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PaymentConfirmRes> response = null;
        try {
            response = restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    entity,
                    PaymentConfirmRes.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new TossPaymentException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        }
        PaymentConfirmRes result = response.getBody();

        // 4. 판매자 포인트 적립
        User seller = marketPost.getSeller();
        seller.earnPoints(marketPost.getPrice());

        // 5. 포인트 적립 내역 기록
        PointTransaction pointTransaction = PointTransaction.earn(seller, marketPost.getPrice());
        pointTransactionRepository.save(pointTransaction);

        // 6. 구매자 정보 세팅 + 판매 상태 변경
        marketPost.markSold(buyer);

        // 7. Transaction 저장
        Transaction transaction = Transaction.create(marketPost, buyer, req.getPaymentKey(), result.getMethod());
        transactionRepository.save(transaction);

        String title = chatRoom.getMarketPost().getTitle().length() > 5 ?
                chatRoom.getMarketPost().getTitle().substring(0, 5) + "..." : chatRoom.getMarketPost().getTitle();
        String notificationContent = title + "이 판매되었습니다!";

        notificationService.create(marketPost.getId(), marketPost.getSeller(), notificationContent, NotificationType.TRADE_SOLD);

        chatRoomRepository.findByMarketPost(marketPost).forEach(cr -> chatRedisService.publishSaleStatusToRedis(cr.getId(), SaleStatus.SOLD));

        return result;
    }
}

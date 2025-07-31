package com.beyond.meongnyang.market.service;

import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.market.dto.MarketPostCreateReq;
import com.beyond.meongnyang.market.dto.MarketPostUpdateReq;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.entity.ProductImage;
import com.beyond.meongnyang.market.repository.MarketPostRepository;
import com.beyond.meongnyang.market.repository.ProductImageRepository;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarketService {
    private final MarketPostRepository marketPostRepository;
    private final ProductImageRepository productImageRepository;
    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

    public Long marketPostCreate(MarketPostCreateReq marketPostCreateReq, List<MultipartFile> imageFiles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

        MarketPost marketPost = marketPostCreateReq.toEntity();
        marketPost.setSeller(user);

        if(imageFiles != null && !imageFiles.isEmpty()){
            List<String> urls = s3UploadService.upload(imageFiles);
            marketPost.setThumbnailImage(urls.get(0));

            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .marketPost(marketPost)
                        .imageUrl(url)
                        .build();
                marketPost.addProductImage(productImage);
            }
        }
        return marketPostRepository.save(marketPost).getId();
    }

    public Long marketPostUpdate(Long id, MarketPostUpdateReq marketPostUpdateReq, List<MultipartFile> imageFiles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));

        MarketPost marketPost = marketPostCreateReq.toEntity();
        marketPost.setSeller(user);

        if(imageFiles != null && !imageFiles.isEmpty()){
            List<String> urls = s3UploadService.upload(imageFiles);
            marketPost.setThumbnailImage(urls.get(0));

            for (String url : urls) {
                ProductImage productImage = ProductImage.builder()
                        .marketPost(marketPost)
                        .imageUrl(url)
                        .build();
                marketPost.addProductImage(productImage);
            }
        }
        return marketPostRepository.save(marketPost).getId();
    }
}

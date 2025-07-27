package com.beyond.meongnyang.market.service;

import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.market.dto.MarketPostCreateRequest;
import com.beyond.meongnyang.market.entity.MarketPost;
import com.beyond.meongnyang.market.repository.CategoryRepository;
import com.beyond.meongnyang.market.repository.MarketPostRepository;
import com.beyond.meongnyang.market.repository.ProductImageRepository;
import com.beyond.meongnyang.user.domain.User;
import lombok.RequiredArgsConstructor;
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
    private final CategoryRepository categoryRepository;
    private final S3UploadService s3UploadService;

    public void createMarketPost(MarketPostCreateRequest marketPostCreateRequest, List<MultipartFile> imageFiles, User seller) {
        MarketPost marketPost = marketPostRepository.save(marketPostCreateRequest.toEntity());

    }
}

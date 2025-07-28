package com.beyond.meongnyang.post.service;

import com.beyond.meongnyang.post.dto.PostCreateReq;
import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.post.entity.*;
import com.beyond.meongnyang.post.repository.PostRepository;
import com.beyond.meongnyang.post.repository.TagRepository;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 일기 작성
    public Long save(PostCreateReq postCreateRequest, List<MultipartFile> files){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("없는 사용자입니다."));
        Post post = postCreateRequest.postToEntity();

        post.setUser(user);

        // 해시태그 처리
        String[] hashtags = Arrays.stream(postCreateRequest.getContent().split("#"))
                .skip(1)
                .map(s -> s.split("\\s|#")[0])
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toArray(String[]::new);

        for(String tagName : hashtags){
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(Tag.builder()
                            .name(tagName)
                            .build()));
            HashTagId hashTagId = new HashTagId(post.getId(), tag.getId());
            HashTag hashTag = HashTag.builder()
                    .id(hashTagId)
                    .post(post)
                    .tag(tag)
                    .build();
            post.addHashTag(hashTag);
        }

        // 파일 처리
        if(files != null && !files.isEmpty()){
            List<String> urls = s3UploadService.upload(files);
            for (String url : urls) {
                Media media = Media.builder()
                        .url(url)
                        .post(post)
                        .build();

                post.addMedia(media);
            }
        }
        return postRepository.save(post).getId();
    }
    // 일기 상세 조회

    // 일기 목록 조회

    // 일기 수정

    // 일기 삭제

    // 좋아요

    // 좋아요 수 카운트

    // 댓글 달기

    // 댓글 수정

    // 대댓글 달기

    // 검색

    // 신고

    // 친구 추천
}

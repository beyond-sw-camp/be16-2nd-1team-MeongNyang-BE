package com.beyond.meongnyang.post.service;

import com.beyond.meongnyang.post.dto.PostCreateRequest;
import com.beyond.meongnyang.post.entity.Post;
import com.beyond.meongnyang.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    // 일기 작성
    public void save(PostCreateRequest postCreateRequest){
        Post post = Post.builder()
                .title("안녕하세요")
                .content(postCreateRequest.getContent())
                .build();
        postRepository.save(post);
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

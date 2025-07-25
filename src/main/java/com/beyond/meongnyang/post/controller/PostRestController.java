package com.beyond.meongnyang.post.controller;

import com.beyond.meongnyang.common.dto.CommonDto;
import com.beyond.meongnyang.post.dto.PostCreateRequest;
import com.beyond.meongnyang.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts/**")
public class PostRestController {
    private final PostService postService;
    // 일기 작성
    @PostMapping("/")
    public ResponseEntity<?> save(@RequestPart(name = "postCreateRequest") @Valid PostCreateRequest postCreateRequest, @RequestPart(name = "files")List<MultipartFile> files){
        postService.save(postCreateRequest, files);
        return new ResponseEntity<>(new CommonDto("ok", HttpStatus.CREATED.value(), "post is created"), HttpStatus.CREATED);
    };

    // 일기 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> postDetail(@PathVariable("id")Long id){
        return null;
    }

    // 일기 목록 조회
    @GetMapping("/")
    public List<?> posts(){
        return null;
    }

    // 일기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> postUpdate(@PathVariable("id")Long id){
        return null;
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> postDelete(@PathVariable("id")Long id){
        return null;
    }

    // 좋아요
    @PostMapping("/like/{id}")
    public ResponseEntity<?> postLike(@PathVariable("id")Long id){
        return null;
    }

    // 좋아요 수 카운트
    @GetMapping("/like/{id}")
    public ResponseEntity<?> postLikeCount(@PathVariable("id")Long id){
        return null;
    }

    // 댓글 달기

    // 댓글 수정

    // 대댓글 달기

    // 검색

    // 신고

    // 친구 추천

    // 팔로워 수 조회

    // 팔로우

    // 언팔로우

    // 차단

    // 대표동물 변경
}

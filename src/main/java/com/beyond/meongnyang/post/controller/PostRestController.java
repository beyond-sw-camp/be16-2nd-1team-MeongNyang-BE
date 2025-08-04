package com.beyond.meongnyang.post.controller;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.beyond.meongnyang.post.dto.*;
import com.beyond.meongnyang.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostRestController {
    private final PostService postService;

    // 일기 작성
    @PostMapping
    public ResponseEntity<?> save(@RequestPart(name = "postCreateRequest") @Valid PostCreateReq postCreateRequest, @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        Long id = postService.save(postCreateRequest, files);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.CREATED.value(),
                        "일기를 작성했습니다."
                ), HttpStatus.CREATED
        );
    }

    // 일기 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> postUpdate(@PathVariable("id") Long id, @RequestPart PostEditReq postEditReq, @RequestPart List<MultipartFile> files) throws AccessDeniedException {
        postService.updatePost(id, postEditReq, files);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "일기를 수정했습니다."
                ), HttpStatus.OK
        );
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> postDelete(@PathVariable("id") Long id) throws AccessDeniedException {
        postService.deletePost(id);
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        id,
                        HttpStatus.OK.value(),
                        "일기를 삭제했습니다."
                ), HttpStatus.OK
        );
    }

    // 일기 목록 조회
    @GetMapping
    public ResponseEntity<?> posts(@PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.myPosts(pageable),
                        HttpStatus.OK.value(),
                        "내 일기를 불러왔습니다."
                ), HttpStatus.OK
        );
    }

    // 일기 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> postDetail(@PathVariable("id") Long postId) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.myPost(postId),
                        HttpStatus.OK.value(),
                        "일기를 불러왔습니다."
                ), HttpStatus.OK
        );
    }



    // 좋아요
    @PostMapping("/like")
    public ResponseEntity<?> postLike(@RequestBody PostLikeReq postLikeReq) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.postLike(postLikeReq.getPostId()),
                        HttpStatus.OK.value(),
                        "성공"
                ), HttpStatus.OK
        );
    }

    // 좋아요 취소
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> postLikeCancel(@PathVariable("id") Long id) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.postLikeCancel(id),
                        HttpStatus.OK.value(),
                        "성공"
                ), HttpStatus.OK
        );
    }

    // 좋아요 목록 조회
    @GetMapping("/{id}/like")
    public ResponseEntity<?> postLikes(@PathVariable("id") Long postId, @PageableDefault(value = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(
                CommonRes.ofSuccess(
                        postService.postLikeList(postId, pageable),
                        HttpStatus.OK.value(),
                        "좋아요 목록 조회 완료"
                ), HttpStatus.OK
        );
    }

//    // 댓글 달기
//    @PostMapping("/comment")
//    public ResponseEntity<?> postCreateComment(@RequestBody PostCommentCreateReq postCommentCreateReq){
//        return new ResponseEntity<>(
//                CommonRes.ofSuccess(
//                        postService.createComment(postCommentCreateReq),
//                        HttpStatus.OK.value(),
//                        "댓글을 작성했습니다."
//                ), HttpStatus.OK
//        );
//    }
//
//    // 댓글 수정
//    @PatchMapping("/comment")
//    public ResponseEntity<?> postEditComment(@RequestBody PostCommentEditReq postCommentEditReq){
//        return new ResponseEntity<>(
//                CommonRes.ofSuccess(
//                        postService.editComment(postCommentEditReq),
//                        HttpStatus.OK.value(),
//                        "댓글을 수정했습니다."
//                ), HttpStatus.OK
//        );
//    }
//
//    // 댓글 삭제
//    @DeleteMapping("/comment")
//    public ResponseEntity<?> postCreateCommentReply(){
//        return new ResponseEntity<>(
//                CommonRes.ofSuccess(
//                        postService.deleteComment(postLikesReq, pageable),
//                        HttpStatus.OK.value(),
//                        "성공"
//                ), HttpStatus.OK
//        );
//    }
//
//    @PostMapping("/comment/{id}")
//    public ResponseEntity<?> postCreateCommentReply(@PathVariable("id") Long commentId){
//        return new ResponseEntity<>(
//                CommonRes.ofSuccess(
//                        postService.deleteComment(postLikesReq, pageable),
//                        HttpStatus.OK.value(),
//                        "성공"
//                ), HttpStatus.OK
//        );
//    }
//
//    // 댓글 목록 조회
//    @GetMapping("/comment")
//    public ResponseEntity<?> postComments(){
//        return new ResponseEntity<>(
//                CommonRes.ofSuccess(
//                        postService.commentList(postLikesReq, pageable),
//                        HttpStatus.OK.value(),
//                        "성공"
//                ), HttpStatus.OK
//        );
//    }

    // 친구 추천

    // 검색

    // 팔로워 수 조회

    // 팔로우

    // 언팔로우

    // 신고

    // 차단
}

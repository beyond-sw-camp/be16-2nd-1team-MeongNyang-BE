package com.beyond.meongnyang.post.service;

import com.beyond.meongnyang.post.dto.*;
import com.beyond.meongnyang.common.S3UploadService;
import com.beyond.meongnyang.post.entity.*;
import com.beyond.meongnyang.post.repository.PostRepository;
import com.beyond.meongnyang.post.repository.TagRepository;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final EntityManager em;

    // 일기 작성
    public Long save(PostCreateReq postCreateRequest, List<MultipartFile> files){
        User user = getCurrentUser();

        Post post = postCreateRequest.postToEntity();
        post.setUser(user);

        handleHashtags(post, postCreateRequest.getContent());
        handleFileUpload(post, files);

        return postRepository.save(post).getId();
    }


    // 일기 수정
    public void updatePost(Long id, PostEditReq postEditReq, List<MultipartFile> files) throws AccessDeniedException {
        User user = getCurrentUser();

        // 원래 일기를 가져온다
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("글이 존재하지 않습니다."));

        // 작성자 확인
        if (!Objects.equals(user.getId(), post.getUser().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 수정 가능합니다.");
        }

        post.updatePost(postEditReq.getTitle(), postEditReq.getContent());
        post.getHashtags().clear();
        post.getMediaList().clear();
        em.flush();
        handleHashtags(post, postEditReq.getContent());
        handleFileUpload(post, files);
    }

    // 일기 삭제(soft-delete)
    public void deletePost(Long id) throws AccessDeniedException {
        User user = getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("글이 존재하지 않습니다."));

        // 작성자 확인
        if (!Objects.equals(user.getId(), post.getUser().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }

        post.deletePost("Y");
    }

    // 내 일기 목록 조회
    public Page<PostListReq> myPosts(Pageable pageable){
        User user = getCurrentUser();
        Page<Post> postList = postRepository.findAllByUserId(user.getId(), pageable);
        return postList.map(p->PostListReq.fromEntity(p));
    }

    // 일기 상세 조회
    public PostDetailRes myPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다"));
        return new PostDetailRes().fromEntity(post);
    }

    // 좋아요

    // 좋아요 수 카운트

    // 댓글 달기

    // 댓글 수정

    // 대댓글 달기

    // 검색

    // 신고

    // 친구 추천

    // 일기 생성 및 수정 시 해시태그 처리
    private void handleHashtags(Post post, String content) {
        String[] hashtags = Arrays.stream(content.split("#"))
                .skip(1)
                .map(s -> s.split("\\s|#")[0])
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toArray(String[]::new);

        for (String tagName : hashtags) {
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
    }
    // 일기 생성 및 식제 시 파일 처리
    private void handleFileUpload(Post post, List<MultipartFile> files){
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
    }

    // 유효한 사용자인지 확인
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("없는 사용자입니다."));
    }
}

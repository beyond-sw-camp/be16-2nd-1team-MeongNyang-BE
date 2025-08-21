package com.beyond.meongnyang.post.service;

import com.beyond.meongnyang.admin.repository.ReportRepository;
import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.pet.entity.Pet;
import com.beyond.meongnyang.pet.repository.PetRepository;
import com.beyond.meongnyang.post.dto.*;
import com.beyond.meongnyang.common.service.S3UploadService;
import com.beyond.meongnyang.post.entity.*;
import com.beyond.meongnyang.post.repository.*;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final PetRepository petRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CommentTagRepository commentTagRepository;
    private final ReportRepository reportRepository;
    private final S3UploadService s3UploadService;
    private final CommonService commonService;
    private final EntityManager em;

    // 일기 작성
    public Long save(PostCreateReq postCreateRequest, List<MultipartFile> files) {
        User user = commonService.getCurrentUser();

        Post post = postCreateRequest.postToEntity();
        post.setUser(user);

        handleHashtags(post, postCreateRequest.getContent());
        handleFileUpload(post, files);

        return postRepository.save(post).getId();
    }


    // 일기 수정
    public void updatePost(Long id, PostEditReq postEditReq, List<MultipartFile> files) throws AccessDeniedException {
        User user = commonService.getCurrentUser();

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
        User user = commonService.getCurrentUser();
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("글이 존재하지 않습니다."));

        // 작성자 확인
        if (!Objects.equals(user.getId(), post.getUser().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }

        post.deletePost("Y");
    }

    // 내 일기 목록 조회
    public Page<PostListReq> myPosts(Pageable pageable) {
        User user = commonService.getCurrentUser();
        Page<Post> postList = postRepository.findAllByUserId(user.getId(), pageable);
        return postList.map(p -> PostListReq.fromEntity(p));
    }

    // 일기 상세 조회
    public PostDetailRes myPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다"));
        Pet pet = petRepository.findByUserIdAndFirstPetAndDelYn(post.getUser().getId(), true, "N").orElseThrow(() -> new EntityNotFoundException("해당 펫이 존재하지 않습니다"));
        int likeCount = likeRepository.countByPostId(postId);
        return PostDetailRes.fromEntity(post, pet, likeCount);
    }

    // 좋아요 처리
    public Long postLike(Long postId) {
        User user = commonService.getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다"));
        boolean isLike = likeRepository.existsByUserIdAndPostId(user.getId(), post.getId());
        if (isLike) {
            throw new EntityExistsException("이미 좋아요를 누른 포스트입니다.");
        } else {
            PostLikeRes postLikeRes = new PostLikeRes();
            return likeRepository.save(postLikeRes.likeToEntity(post, user)).getId();
        }
    }

    // 좋아요 취소
    public Long postLikeCancel(Long postId) {
        User user = commonService.getCurrentUser();
        likeRepository.deleteByPostIdAndUserId(postId, user.getId());
        return postId;
    }

    // 좋아요 목록
    public Page<PostLikeListRes> postLikeList(Long postId, Pageable pageable) {
        return likeRepository.findAllByPostId(postId, pageable)
                .map(like -> PostLikeListRes.fromEntity(like.getPost(), like.getUser()));
    }

    // 댓글 달기
    public Long createComment(Long postId, PostCommentCreateReq postCommentCreateReq) {
        User user = commonService.getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("게시글 없음"));
        return commentRepository.save(postCommentCreateReq.toEntity(user, post)).getId();
    }

    // 대댓글 달기
    public Long createReply(Long commentId, PostCommentReplyReq request) {
        User replyUser = commonService.getCurrentUser();
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));
        User mentionUser = userRepository.findById(request.getMentionUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        Comment replyComment = request.ReplyToEntity(replyUser, parentComment.getPost());
        commentRepository.save(replyComment);

        CommentTag tag = request.CommentTagToEntity(replyComment, mentionUser, replyUser, parentComment);
        commentTagRepository.save(tag);

        return replyComment.getId();
    }

    // 댓글 목록
    public Page<PostCommentListRes> commentList(Long postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findAllByPostId(postId, pageable);
        return comments.map(comment -> {
            List<CommentTag> tags = commentTagRepository.findAllByParentComment(comment);
            List<PostCommentReplyRes> replies = tags.stream()
                    .map(PostCommentReplyRes::fromEntity)
                    .collect(Collectors.toList());
            return PostCommentListRes.fromEntity(comment, replies);
        });
    }

    // 댓글 수정
    public Long editComment(Long commentId, PostCommentEditReq postCommentEditReq) throws AccessDeniedException {
        User user = commonService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        // 작성자 확인
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 수정 가능합니다.");
        }

        comment.updateContent(postCommentEditReq.getContent());
        return commentId;
    }

    // 댓글 삭제
    public Long deleteComment(Long commentId) throws AccessDeniedException {
        User user = commonService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제 가능합니다.");
        }
        comment.softDelete();
        return commentId;
    }

    // 검색
    public Page<PostSearchRes> searchPost(SearchType type, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 키워드 없으면 빈 결과 반환(또는 IllegalArgumentException 던져도 됨)
            throw new IllegalArgumentException("지원하지 않는 검색 타입입니다.");
        }
        final String like = "%" + keyword.trim() + "%";

        Specification<Post> spec = (root, query, cb) -> {
            query.distinct(true); // hashtag 조인 시 중복 제거
            switch (type) {
                case TITLE -> {
                    return cb.like(root.get("title"), like);
                }
                case CONTENT -> {
                    return cb.like(root.get("content"), like);
                }
                case USER -> {
                    // user.name / user.nickname 등 실제 필드명으로 교체
                    Join<Post, User> user = root.join("user", JoinType.INNER);
                    return cb.or(
                            cb.like(user.get("name"), like),
                            cb.like(user.get("nickname"), like)
                    );
                }
                case HASHTAG -> {
                    Join<Post, HashTag> hashTag = root.join("hashtags", JoinType.INNER);
                    Join<HashTag, Tag> tag = hashTag.join("tag", JoinType.INNER);
                    return cb.like(tag.get("name"), like);
                }
                default -> throw new IllegalArgumentException("지원하지 않는 검색 타입입니다.");
            }
        };
        return postRepository.findAll(spec, pageable)
                .map(post -> {
                    Pet pet = petRepository.findByUserIdAndFirstPetAndDelYn(
                            post.getUser().getId(), true, "N"
                    ).orElseThrow(() -> new EntityNotFoundException("해당 펫이 존재하지 않습니다"));
                    return PostSearchRes.fromEntity(post, pet);
                });
    }

    // 일기 신고하기
    public void reportPost(Long postId, PostReportCreateReq postReportCreateReq) {
        User reportUser = commonService.getCurrentUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("해당 일기가 존재하지 않습니다."));
        reportRepository.save(postReportCreateReq.ReportToEntity(post, reportUser));
    }

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
    private void handleFileUpload(Post post, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
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
}

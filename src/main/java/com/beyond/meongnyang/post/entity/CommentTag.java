package com.beyond.meongnyang.post.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentTag extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; // 대댓글 본문

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_user_id")
    private User commentUser; // 멘션 당한 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_user_id")
    private User replyUser; // 대댓글 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 부모 댓글 명시적으로 연결
}


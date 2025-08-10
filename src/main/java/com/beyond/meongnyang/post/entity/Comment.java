package com.beyond.meongnyang.post.entity;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

// Comment.java (Entity)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "del_yn = false")
public class Comment extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Bool delYn = Bool.FALSE;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentTag> commentTags = new ArrayList<>();

    public void updateContent(String content) {
        this.content = content;
    }

    public void softDelete() {
        this.delYn = Bool.TRUE;
    }
}
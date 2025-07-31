package com.beyond.meongnyang.post.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.post.dto.PostListReq;
import com.beyond.meongnyang.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "post")
@Where(clause = "del_yn = 'N'")
public class Post extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content", nullable = false)
    String content;

    @Builder.Default
    private String delYn="N";

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<HashTag> hashtags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Media> mediaList = new ArrayList<>();

    public void addMedia(Media media) {
        this.mediaList.add(media);
    }

    public void addHashTag(HashTag hashTag){
        this.hashtags.add(hashTag);
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void updatePost(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void deletePost(String delYn){
        this.delYn = delYn;
    }

//    @OneToMany(mappedBy = "marketPost")
//    private List<MarketPost> marketPosts;
}

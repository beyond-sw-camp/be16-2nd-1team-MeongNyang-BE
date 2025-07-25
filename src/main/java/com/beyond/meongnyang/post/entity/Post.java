package com.beyond.meongnyang.post.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "post")
public class Post extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "content", nullable = false)
    String content;

    @OneToMany(mappedBy = "post")
    private List<HashTag> hashtags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> mediaList;

    public void addMedia(Media media) {
        this.mediaList.add(media);
    }


//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

//    @OneToMany(mappedBy = "marketPost")
//    private List<MarketPost> marketPosts;
}

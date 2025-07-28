package com.beyond.meongnyang.post.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class HashTagId implements Serializable {
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "tag_id")
    private Long tagId;

    // equals(), hashCode() 필수!
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashTagId)) return false;
        HashTagId that = (HashTagId) o;
        return Objects.equals(postId, that.postId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, tagId);
    }
}

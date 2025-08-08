package com.beyond.meongnyang.user.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "follow")
public class Follow extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name  = "follower_id")
    private User follower;

    @ManyToOne
    @JoinColumn(name  = "follow_id")
    private User follow;
}

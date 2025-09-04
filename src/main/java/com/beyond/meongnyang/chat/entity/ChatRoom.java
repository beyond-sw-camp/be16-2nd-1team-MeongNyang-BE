package com.beyond.meongnyang.chat.entity;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.market.entity.MarketPost;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "market_post_id")
    private MarketPost marketPost;

    @Builder.Default
    @Column(name = "is_purchase_approved", nullable = false)
    @Enumerated(EnumType.STRING)
    private Bool isPurchaseApproved = Bool.FALSE;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> chatParticipantList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    public Boolean updateIsPurchaseApproved() {
        this.isPurchaseApproved = this.isPurchaseApproved == Bool.TRUE ? Bool.FALSE : Bool.TRUE;
        return this.isPurchaseApproved == Bool.TRUE;
    }
}

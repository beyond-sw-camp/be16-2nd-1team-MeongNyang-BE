package com.beyond.meongnyang.chat.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_participant")
public class ChatParticipant extends CommonAt {
    // TODO : ChatParticipant를 @EqualsAndHashCode(of = {"user", "chatRoom"}) 어노테이션으로 추가 후 Set으로 변경하는 것 고민해보기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
}

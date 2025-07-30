package com.beyond.meongnyang.chat.entity;

import com.beyond.meongnyang.common.domain.CommonAt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_room")
public class ChatRoom extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "is_group_chat", nullable = false)
    private Boolean isGroupChat = Boolean.FALSE;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatParticipant> chatParticipantList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    public void updateIsGroupChat(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }
}

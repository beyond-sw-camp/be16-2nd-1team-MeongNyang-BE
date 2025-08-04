//package com.beyond.meongnyang.chat.entity;
//
//import com.beyond.meongnyang.common.domain.Bool;
//import com.beyond.meongnyang.common.domain.CommonAt;
//import com.beyond.meongnyang.user.entity.User;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "chat_message_status")
//public class ChatMessageStatus extends CommonAt {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_room_id", nullable = false)
//    private ChatRoom chatRoom;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "chat_message_id", nullable = false)
//    private ChatMessage chatMessage;
//
//    @Builder.Default
//    @Column(name = "is_read",  nullable = false)
//    @Enumerated(EnumType.STRING)
//    private Bool isRead = Bool.FALSE;
//
//    public void read() {
//        this.isRead = Bool.TRUE;
//    }
//}
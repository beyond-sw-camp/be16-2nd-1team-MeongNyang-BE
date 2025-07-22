package com.beyond.meongnyang.chat.entity;

import com.beyond.meongnyang.common.entity.CommonAt;
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
public class ChatMedia extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;
    @Column(columnDefinition = "TEXT")
    private String url;
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
}

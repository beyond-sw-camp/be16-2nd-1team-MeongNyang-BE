package com.beyond.meongnyang.chat.entity;

import com.beyond.meongnyang.common.entity.CommonAt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Message> messageList = new ArrayList<>();
}

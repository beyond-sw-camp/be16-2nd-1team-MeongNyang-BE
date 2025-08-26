package com.beyond.meongnyang.notification.entity;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.domain.CommonAt;
import com.beyond.meongnyang.user.entity.User;
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
@Table(name = "notification")
public class Notification extends CommonAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "content")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type")
    private NotificationType notificationType;
    @Column(name = "target_id")
    private Long targetId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Bool isRead = Bool.FALSE;

    public void read() {
        this.isRead = Bool.TRUE;
    }
}

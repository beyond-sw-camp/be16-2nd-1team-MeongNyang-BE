package com.beyond.meongnyang.notification.repository;

import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.notification.entity.Notification;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(User user);

    void deleteAllByReceiver(User receiver);

    List<Notification> findAllByReceiverAndIsRead(User receiver, Bool isRead);
}

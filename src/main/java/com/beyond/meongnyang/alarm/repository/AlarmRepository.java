package com.beyond.meongnyang.alarm.repository;

import com.beyond.meongnyang.alarm.dto.AlarmRes;
import com.beyond.meongnyang.alarm.entity.Alarm;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByReceiver(User user);

    void deleteAllByReceiver(User receiver);

    List<Alarm> findAllByReceiverAndIsRead(User receiver, Bool isRead);
}

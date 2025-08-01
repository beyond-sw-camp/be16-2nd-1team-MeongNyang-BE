package com.beyond.meongnyang.chat.repository;

import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findAllByUser(User user);
}

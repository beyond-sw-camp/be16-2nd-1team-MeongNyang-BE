package com.beyond.meongnyang.chat.repository;

import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findAllByUser(User user);

    List<ChatParticipant> findAllByChatRoomId(Long roomId);

    Optional<ChatParticipant> findByUserEmailAndChatRoomId(String userEmail, Long chatRoomId);

    Optional<ChatParticipant> findByUserEmailAndChatRoom(String userEmail, ChatRoom chatRoom);
}

package com.beyond.meongnyang.chat.repository;

import com.beyond.meongnyang.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr " +
            "FROM ChatRoom cr " +
            "JOIN cr.chatParticipantList cp " +
            "WHERE cp.user.id = :userId " +
            "AND cr.marketPost.id = :marketPostId")
    Optional<ChatRoom> findByMarketPostIdAndParticipant(Long marketPostId, Long userId);
}

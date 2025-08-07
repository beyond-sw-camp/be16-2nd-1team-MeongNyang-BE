package com.beyond.meongnyang;

import com.beyond.meongnyang.chat.repository.ChatParticipantRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.chat.service.ChatService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ChatServiceTest {
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatParticipantRepository chatParticipantRepository;

    @Test
    public void chatMessageReadTest() { // 임시 테스트
        chatService.readMessages(2L, "test@naver.com");

        Assertions.assertEquals(49L, chatParticipantRepository.findByUserEmailAndChatRoomId("test@naver.com", 2L).get().getLastReadMessage().getId());;
    }
}

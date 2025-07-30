package com.beyond.meongnyang.chat;

import com.beyond.meongnyang.chat.dto.ChatMessageReq;
import com.beyond.meongnyang.chat.dto.ChatRoomCreateReq;
import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.chat.entity.ChatMessageStatus;
import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.chat.repository.ChatMessageRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void saveMessage(Long id, ChatMessageReq chatMessageReq) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));


        // 보낸사람 조회
        User user = userRepository.findByEmail(chatMessageReq.getSenderEmail()).orElseThrow(() -> new EntityNotFoundException("Sender Email Not Found"));


        // 메세지 객체 생성
        ChatMessage chatMessage = ChatMessage.builder()
                .user(user)
                .chatRoom(chatRoom)
                .content(chatMessageReq.getMessage())
                .build();

        // 유저별 메세지상태 여부 저장
        chatRoom.getChatParticipantList().forEach(chatParticipant -> {
            // 유저별 메세지 상태 객체 생성
            ChatMessageStatus chatMessageStatus = ChatMessageStatus.builder()
                    .chatMessage(chatMessage)
                    .chatRoom(chatRoom)
                    .user(chatParticipant.getUser())
                    .build();

            // 유저별 메세지 상태 객체 저장
            chatMessage.getChatMessageStatusList().add(chatMessageStatus);

            if (chatParticipant.getUser().equals(user)) chatMessageStatus.read();
        });

        // 메세지 저장
        chatMessageRepository.save(chatMessage);
        // TODO : 메세지에 파일(사진, 오디오, 동영상)이 있을 경우 처리해야 함
    }

    public Long createChatRoom(ChatRoomCreateReq chatRoomCreateReq) {
        // 채팅방 객체 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomCreateReq.getRoomName())
                .build();

        // 채팅방 참여자 객체(채팅방 개설을 요구한 유저의 객체) 생성
        chatRoomCreateReq.getUserEmailList().forEach(userEmail -> {
            ChatParticipant chatParticipant = ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    // TODO : soft-delete된 유저도 제외하도록 개선 필요
                    .user(userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User Not Found")))
                    .build();

            chatRoom.getChatParticipantList().add(chatParticipant);
        });

        // 참여자가 2명을 넘기면 그룹챗으로 상태 변경
        if (chatRoom.getChatParticipantList().size() > 2) chatRoom.updateIsGroupChat(Boolean.TRUE);

        // 채팅방 참여자 저장
        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }
}

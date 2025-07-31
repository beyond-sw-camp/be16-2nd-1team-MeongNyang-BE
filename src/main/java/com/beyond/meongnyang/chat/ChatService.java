package com.beyond.meongnyang.chat;

import com.beyond.meongnyang.chat.dto.ChatMessageReq;
import com.beyond.meongnyang.chat.dto.ChatParticipantAddReq;
import com.beyond.meongnyang.chat.dto.ChatRoomCreateReq;
import com.beyond.meongnyang.chat.dto.ChatRoomSummaryRes;
import com.beyond.meongnyang.chat.entity.ChatMessage;
import com.beyond.meongnyang.chat.entity.ChatMessageStatus;
import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.chat.repository.ChatMessageRepository;
import com.beyond.meongnyang.chat.repository.ChatParticipantRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.user.domain.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;

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
        if (chatRoom.getChatParticipantList().size() > 2) chatRoom.updateIsGroupChat(Bool.TRUE);

        // 채팅방 참여자 저장
        chatRoomRepository.save(chatRoom);

        return chatRoom.getId();
    }

    public List<ChatRoomSummaryRes> getMyChatRooms() {
        User user = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        List<ChatParticipant> chatParticipantList = this.chatParticipantRepository.findAllByUser(user);

//        return chatParticipantList.stream().map(chatParticipant -> ChatRoomSummaryRes.fromEntity(chatParticipant.getChatRoom())).toList();
        return chatParticipantList.stream().map(ChatParticipant::getChatRoom).map(ChatRoomSummaryRes::fromEntity).toList();
    }

    // TODO : 응답해줄 것 고민하기
    public void inviteUsers(Long roomId, List<ChatParticipantAddReq> chatParticipantAddReqList) {
        ChatRoom chatRoom = this.chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        User inviter = this.userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Set<Long> participantIdSet = chatRoom.getChatParticipantList().stream().map(ChatParticipant::getUser).map(User::getId).collect(Collectors.toSet());
        if (!participantIdSet.contains(inviter.getId())) throw new EntityNotFoundException("Inviter Not Found"); // 초대한 유저 검증


        chatParticipantAddReqList.forEach(chatParticipantAddReq -> {
            User user = this.userRepository.findByEmail(chatParticipantAddReq.getInviteeEmail()).orElseThrow(() -> new EntityNotFoundException("Invitee Not Found"));

            ChatParticipant chatParticipant = ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .build();

            // 혹시나 이미 채팅방에 참여 중인 유저가 초대한 유저 목록에 넘어왔을 경우(참여자 테이블에 중복으로 쌓이면 머리 아플 것 같아서 데이터 무결성 강화)
            if (!participantIdSet.contains(user.getId())) chatRoom.getChatParticipantList().add(chatParticipant);
        });

        if (chatRoom.getChatParticipantList().size() > 2) chatRoom.updateIsGroupChat(Bool.TRUE);
    }
}

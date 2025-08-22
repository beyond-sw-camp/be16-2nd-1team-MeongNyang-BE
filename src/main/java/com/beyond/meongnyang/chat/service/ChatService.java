package com.beyond.meongnyang.chat.service;

import com.beyond.meongnyang.admin.repository.ReportRepository;
import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.entity.*;
import com.beyond.meongnyang.chat.repository.ChatMessageRepository;
import com.beyond.meongnyang.chat.repository.ChatParticipantRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.common.service.CommonService;
import com.beyond.meongnyang.common.service.S3UploadService;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.user.entity.User;
import com.beyond.meongnyang.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ReportRepository reportRepository;
    private final S3UploadService s3UploadService;
    private final CommonService commonService;

//    @Autowired
//    public ChatService(UserRepository userRepository, ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository, ChatParticipantRepository chatParticipantRepository) {
//        this.userRepository = userRepository;
//        this.chatRoomRepository = chatRoomRepository;
//        this.chatMessageRepository = chatMessageRepository;
//        this.chatParticipantRepository = chatParticipantRepository;
//    }

    public ChatMessageRes saveMessage(Long id, ChatMessageReq chatMessageReq) {
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

//        chatRoom.getChatParticipantList().stream()
//                .filter(cp -> cp.getUser().getId().equals(user.getId())).findFirst()
//                .ifPresent(cp -> cp.read(chatMessage));

        // 유저별 메세지상태 여부 저장
//        chatRoom.getChatParticipantList().forEach(chatParticipant -> {
//            // 유저별 메세지 상태 객체 생성
//            ChatMessageStatus chatMessageStatus = ChatMessageStatus.builder()
//                    .lastReadMessage(lastReadMessage)
//                    .chatRoom(chatRoom)
//                    .user(chatParticipant.getUser())
//                    .build();
//
//            // 유저별 메세지 상태 객체 저장
//            lastReadMessage.getChatMessageStatusList().add(chatMessageStatus);
//
//            if (chatParticipant.getUser().equals(user)) chatMessageStatus.read();
//        });

        // 메세지 저장
        chatMessageReq.getFileUrls().forEach(url -> {
            ChatMedia chatMedia = ChatMedia.builder()
                    .url(url)
                    .chatMessage(chatMessage)
                    .build();

            chatMessage.getChatMediaList().add(chatMedia);
        });


        chatMessageRepository.save(chatMessage);
        return ChatMessageRes.fromEntity(chatMessage);
    }

    public ChatRoomSummaryRes createChatRoom(ChatRoomCreateReq chatRoomCreateReq) {
        // 채팅방 객체 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomCreateReq.getRoomName())
                .build();

        // 채팅방 참여자 객체 생성
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

        return ChatRoomSummaryRes.fromEntity(chatRoom, 0);
    }

    public List<ChatRoomSummaryRes> getMyChatRooms() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));
        List<ChatParticipant> myChatParticipantList = chatParticipantRepository.findAllByUser(user);

        return myChatParticipantList.stream().map(myChatParticipant -> {
            int newMessageCount = myChatParticipant.getChatRoom().getChatMessageList().size();
            ChatMessage lastReadMessage = myChatParticipant.getLastReadMessage();

            if (lastReadMessage != null)
                newMessageCount = (int) myChatParticipant.getChatRoom().getChatMessageList().stream()
                        .map(ChatMessage::getCreatedAt)
                        .filter(createdAt -> createdAt.isAfter(lastReadMessage.getCreatedAt()))
                        .count();

            return ChatRoomSummaryRes.fromEntity(myChatParticipant.getChatRoom(), newMessageCount);
        }).toList();
//        return myChatParticipantList.stream()
//                .map(ChatParticipant::getChatRoom)
//                .map(ChatRoomSummaryRes::fromEntity)
//                .toList();
    }

    public List<ChatParticipantAddRes> inviteUsers(Long roomId, List<ChatParticipantAddReq> chatParticipantAddReqList) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        User inviter = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        // 채팅 참여자 목록
        Set<Long> participantIdSet = chatRoom.getChatParticipantList().stream().map(ChatParticipant::getUser).map(User::getId).collect(Collectors.toSet());

        if (!participantIdSet.contains(inviter.getId()))
            throw new AccessDeniedException("Access Denied"); // 초대한 유저 검증(속해 있는 유저만 초대를 보낼 수 있게)


        chatParticipantAddReqList.forEach(chatParticipantAddReq -> {
            User user = userRepository.findByEmail(chatParticipantAddReq.getInviteeEmail()).orElseThrow(() -> new EntityNotFoundException("Invitee Not Found"));

            ChatParticipant chatParticipant = ChatParticipant.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .build();

            // 혹시나 이미 채팅방에 참여 중인 유저가 초대한 유저 목록에 넘어왔을 경우(참여자 테이블에 중복으로 쌓이면 머리 아플 것 같아서 데이터 무결성 강화)
            if (!participantIdSet.contains(user.getId())) {
                participantIdSet.add(user.getId());
                chatRoom.getChatParticipantList().add(chatParticipant);
            }
        });

        if (chatRoom.getChatParticipantList().size() > 2) chatRoom.updateIsGroupChat(Bool.TRUE);

        return chatParticipantAddReqList.stream()
                .map(req ->
                        ChatParticipantAddRes.builder().inviteeEmail(req.getInviteeEmail()).build()
                ).toList();
    }

    public List<ChatMessageRes> getChatMessages(Long roomId) {
        validChatRoomParticipant(roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        return chatMessageRepository.findAllByChatRoomOrderByCreatedAt(chatRoom).stream()
                .map(ChatMessageRes::fromEntity).toList();
    }

    public ChatParticipantRemRes leaveChatRoomAndRemoveIfEmpty(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        chatRoom.getChatParticipantList().removeIf(chatParticipant -> chatParticipant.getUser().getEmail().equals(userEmail));

        if (chatRoom.getChatParticipantList().isEmpty()) chatRoomRepository.delete(chatRoom);

        return ChatParticipantRemRes.builder().leftUserEmail(userEmail).build();
    }

    public List<ChatParticipantRes> getChatParticipants(Long roomId) {
        validChatRoomParticipant(roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        return chatRoom.getChatParticipantList().stream().map(ChatParticipantRes::fromEntity).toList();
    }

    public void validChatRoomParticipant(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        chatRoom.getChatParticipantList().stream().map(ChatParticipant::getUser).map(User::getEmail)
                .filter(pEmail -> pEmail.equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst().orElseThrow(() -> new AccessDeniedException("Access Denied"));
    }

    public void validChatRoomParticipant(Long roomId, String email) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));

        // 채팅방에 속해있는 유저인지 검증
        chatRoom.getChatParticipantList().stream().map(ChatParticipant::getUser).map(User::getEmail)
                .filter(pEmail -> pEmail.equals(email))
                .findFirst().orElseThrow(() -> new AccessDeniedException("Access Denied"));
    }

//    public void readMessages(Long roomId) {
//        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        ChatParticipant chatParticipant = chatParticipantRepository.findByUserEmailAndChatRoom(email, chatRoom).orElseThrow(() -> new EntityNotFoundException("participant not found"));
//
//        chatParticipant.read(chatRoom.getChatParticipantList().get(chatRoom.getChatMessageList().size() - 1).getLastReadMessage());
//    }

    public void readMessages(Long roomId,String userEmail) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));
        ChatParticipant chatParticipant = chatParticipantRepository.findByUserEmailAndChatRoom(userEmail, chatRoom).orElseThrow(() -> new EntityNotFoundException("participant not found"));

        chatParticipant.read(chatRoom.getChatMessageList().get(chatRoom.getChatMessageList().size() - 1));
    }

    public List<String> uploadFiles(Long roomId, List<MultipartFile> files) {
        validChatRoomParticipant(roomId);

        LocalDateTime now = LocalDateTime.now();
        String pattern = String.format("chat/%d/%d/%02d/%02d/%s-*", roomId, now.getYear(), now.getMonthValue(), now.getDayOfMonth(), UUID.randomUUID());
        return s3UploadService.upload(files, pattern);
    }

    // 채팅 신고하기
    // ToDo : 컨트롤러 부분만 설계 부탁드립니다.
    public void reportChatMessage(Long chatMessageId, ChatMessageReportCreateReq chatMessageReportCreateReq) {
        User reportUser = commonService.getCurrentUser();
        ChatMessage chatMessage = chatMessageRepository.findById(chatMessageId).orElseThrow(()-> new EntityNotFoundException("해당 채팅 메시지가 존재하지 않습니다."));
        reportRepository.save(chatMessageReportCreateReq.ReportToEntity(chatMessage, reportUser));
    }
}

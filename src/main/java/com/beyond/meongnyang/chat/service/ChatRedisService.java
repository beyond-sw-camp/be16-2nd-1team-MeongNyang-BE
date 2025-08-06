package com.beyond.meongnyang.chat.service;

import com.beyond.meongnyang.chat.dto.ChatMessageRes;
import com.beyond.meongnyang.chat.dto.ChatParticipantAddReq;
import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.chat.repository.ChatParticipantRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ChatRedisService implements MessageListener {
    private final ChatParticipantRepository chatParticipantRepository;

    private final SimpMessageSendingOperations messageTemplate;
    private final RedisTemplate<String, String> chatMessageRedisTemplate;
    private final RedisTemplate<String, Map<String, String>> chatParticipantsRedisTemplate;
    private final RedisTemplate<String, String> chatOnlineParticipantsRedisTemplate;

    private static final String CHAT_ROOM_PREFIX = "CHAT_ROOM_";

    public ChatRedisService(ChatParticipantRepository chatParticipantRepository,
                            SimpMessageSendingOperations messageTemplate,
                            @Qualifier("chatMessage") RedisTemplate<String, String> chatMessageRedisTemplate,
                            @Qualifier("chatParticipants") RedisTemplate<String, Map<String, String>> chatParticipantsRedisTemplate,
                            @Qualifier("chatOnlineParticipants") RedisTemplate<String, String> chatOnlineParticipantsRedisTemplate) {

        this.chatParticipantRepository = chatParticipantRepository;

        this.messageTemplate = messageTemplate;
        this.chatMessageRedisTemplate = chatMessageRedisTemplate;
        this.chatParticipantsRedisTemplate = chatParticipantsRedisTemplate;
        this.chatOnlineParticipantsRedisTemplate = chatOnlineParticipantsRedisTemplate;
    }

    public void publishChatMessageToRedis(Long roomId, ChatMessageRes chatMessageRes) {
        Set<String> onlineParticipants = chatOnlineParticipantsRedisTemplate.opsForSet().members(CHAT_ROOM_PREFIX + roomId);
        Map<String, String> chatParticipants = chatParticipantsRedisTemplate.opsForValue().get(CHAT_ROOM_PREFIX + roomId);

        onlineParticipants.forEach(onlineParticipant -> {
            chatParticipants.put(onlineParticipant, String.valueOf(chatMessageRes.getMessageId()));
        });

        chatParticipantsRedisTemplate.opsForValue().set(CHAT_ROOM_PREFIX + roomId, chatParticipants);

        chatMessageRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-message", chatMessageRes);
    }

    public void publishLeftUserToRedis(Long roomId) {
        Map<String, String> redisChatParticipantMap = getOrLoadParticipantMap(roomId);

        redisChatParticipantMap.remove(SecurityContextHolder.getContext().getAuthentication().getName());
        chatParticipantsRedisTemplate.opsForValue().set(CHAT_ROOM_PREFIX + roomId, redisChatParticipantMap);

        chatParticipantsRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", redisChatParticipantMap);
    }

    public void publishInvitedUsersToRedis(Long roomId, List<ChatParticipantAddReq> chatParticipantAddReqs) {
        Map<String, String> redisChatParticipantMap = getOrLoadParticipantMap(roomId);

        for (ChatParticipantAddReq chatParticipantAddReq : chatParticipantAddReqs) {
            redisChatParticipantMap.put(chatParticipantAddReq.getInviteeEmail(), "0");
        }

        chatParticipantsRedisTemplate.opsForValue().set(CHAT_ROOM_PREFIX + roomId, redisChatParticipantMap);

        chatParticipantsRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", redisChatParticipantMap);
    }

    private Map<String, String> getOrLoadParticipantMap(Long roomId) {
        Map<String, String> participantMap = chatParticipantsRedisTemplate.opsForValue().get(CHAT_ROOM_PREFIX + roomId);

        if (participantMap == null || participantMap.isEmpty()) {
            // 레디스에 없으면 db에서 조회해서 map을 만들어 줌
            List<ChatParticipant> mariaDBChatParticipantList = chatParticipantRepository.findAllByChatRoomId(roomId);

            participantMap = new HashMap<>();

            for (ChatParticipant chatParticipant : mariaDBChatParticipantList) {
                Long lastReadChatMessageId = chatParticipant.getLastReadMessage() == null ? 0 : chatParticipant.getLastReadMessage().getId();
                participantMap.put(chatParticipant.getUser().getEmail(), String.valueOf(lastReadChatMessageId));
            }
        }
        return participantMap;
    }

    public void publishChatOnlineToRedis(Long roomId) {
        chatOnlineParticipantsRedisTemplate.opsForSet().add(CHAT_ROOM_PREFIX + roomId, SecurityContextHolder.getContext().getAuthentication().getName());

        Set<String> onlineParticipants = chatOnlineParticipantsRedisTemplate.opsForSet().members(CHAT_ROOM_PREFIX + roomId);
        chatOnlineParticipantsRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", onlineParticipants);
    }

    // TODO : RDB에 읽음처리
    public void publishChatOfflineToRedis(Long roomId) {
        chatOnlineParticipantsRedisTemplate.opsForSet().remove(CHAT_ROOM_PREFIX + roomId, SecurityContextHolder.getContext().getAuthentication().getName());

        Set<String> onlineParticipants = chatOnlineParticipantsRedisTemplate.opsForSet().members(CHAT_ROOM_PREFIX + roomId);
        chatOnlineParticipantsRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", onlineParticipants);
    }


    // 레디스의 메세지를 받았을 때

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String roomId = channel.split("/")[3];

        switch (channel) {
            case "/topic/chat-rooms/*/chat-message" -> publishChatMessageToStompClient(roomId, message);
            case "/topic/chat-rooms/*/chat-participants" -> publishChatParticipantsToStompClient(roomId, message);
            case "/topic/chat-rooms/*/chat-online-participants" ->
                    publishChatOnlineParticipantsToStompClient(roomId, message);
        }
    }

    public void publishChatMessageToStompClient(String roomId, Message message) {
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-message", message.getBody());
    }

    public void publishChatParticipantsToStompClient(String roomId, Message message) {
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", message.getBody());
    }

    public void publishChatOnlineParticipantsToStompClient(String roomId, Message message) {
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", message.getBody());
    }
}

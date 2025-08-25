package com.beyond.meongnyang.chat.service;

import com.beyond.meongnyang.chat.dto.*;
import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.chat.entity.ChatRoom;
import com.beyond.meongnyang.chat.repository.ChatParticipantRepository;
import com.beyond.meongnyang.chat.repository.ChatRoomRepository;
import com.beyond.meongnyang.common.registry.SseEmitterRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ChatRedisService implements MessageListener {
    private final ObjectMapper objectMapper;

    private final SseEmitterRegistry sseEmitterRegistry;
    private final ChatParticipantRepository chatParticipantRepository;

    private final SimpMessageSendingOperations messageTemplate;
    private final RedisTemplate<String, String> chatPubsubRedisTemplate;
    private final RedisTemplate<String, Map<String, String>> chatParticipantsRedisTemplate;
    private final RedisTemplate<String, String> chatOnlineParticipantsRedisTemplate;

    private static final String PARTICIPANTS_KEY_PREFIX = "CHAT_PARTICIPANTS_";
    private static final String ONLINE_KEY_PREFIX = "CHAT_ONLINE_";
    private final ChatRoomRepository chatRoomRepository;

    public ChatRedisService(SseEmitterRegistry sseEmitterRegistry,
                            ChatParticipantRepository chatParticipantRepository,
                            SimpMessageSendingOperations messageTemplate,
                            @Qualifier("chatPubSub") RedisTemplate<String, String> pubsubRedisTemplate,
                            @Qualifier("chatParticipants") RedisTemplate<String, Map<String, String>> chatParticipantsRedisTemplate,
                            @Qualifier("chatOnlineParticipants") RedisTemplate<String, String> chatOnlineParticipantsRedisTemplate,
                            ObjectMapper objectMapper, ChatRoomRepository chatRoomRepository) {

        this.sseEmitterRegistry = sseEmitterRegistry;

        this.chatParticipantRepository = chatParticipantRepository;

        this.messageTemplate = messageTemplate;
        this.chatPubsubRedisTemplate = pubsubRedisTemplate;
        this.chatParticipantsRedisTemplate = chatParticipantsRedisTemplate;
        this.chatOnlineParticipantsRedisTemplate = chatOnlineParticipantsRedisTemplate;
        this.objectMapper = objectMapper;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void publishChatMessageToRedis(Long roomId, ChatMessageRes chatMessageRes) {
        Set<String> onlineParticipants = chatOnlineParticipantsRedisTemplate.opsForSet().members(ONLINE_KEY_PREFIX + roomId);
        Map<String, String> chatParticipants = getOrLoadParticipantMap(roomId);

        // 메세지를 보내면 온라인 유저들의 마지막 읽은 메세지를 redis에서 갱신
        onlineParticipants.forEach(onlineParticipant -> {
            chatParticipants.put(onlineParticipant, String.valueOf(chatMessageRes.getId()));
        });

        chatParticipantsRedisTemplate.opsForValue().set(PARTICIPANTS_KEY_PREFIX + roomId, chatParticipants);

        String data = null;
        try {
            data = objectMapper.writeValueAsString(chatMessageRes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // 온라인 유저들의 마지막 읽은 메세지는 프론트로 돌려주지 않고 프론트가 알아서 계산하게 하고, 메세지만 프론트로 보내줌
        chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-message", data);
    }

    // redis의 참여자 목록에서 제거하고 publish
    public void publishLeftUserToRedis(Long roomId) {
        Map<String, String> redisChatParticipantMap = getOrLoadParticipantMap(roomId);

        redisChatParticipantMap.remove(SecurityContextHolder.getContext().getAuthentication().getName());
        chatParticipantsRedisTemplate.opsForValue().set(PARTICIPANTS_KEY_PREFIX + roomId, redisChatParticipantMap);

        chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", toJson(roomId, redisChatParticipantMap));
    }

    // redis의 참여자 목록에 추가하고 publish
    public void publishInvitedUsersToRedis(Long roomId, List<ChatParticipantAddRes> chatParticipantAddResList) {
        Map<String, String> redisChatParticipantMap = getOrLoadParticipantMap(roomId);

        for (ChatParticipantAddRes chatParticipantAddRes : chatParticipantAddResList) {
            redisChatParticipantMap.put(chatParticipantAddRes.getInviteeEmail(), "0");
        }

        chatParticipantsRedisTemplate.opsForValue().set(PARTICIPANTS_KEY_PREFIX + roomId, redisChatParticipantMap);
        chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", toJson(roomId, redisChatParticipantMap));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room Not Found"));
        ChatRoomSummaryRes chatRoomSummaryRes = ChatRoomSummaryRes.fromEntity(chatRoom, chatRoom.getChatMessageList().size());
        try {
            String data = objectMapper.writeValueAsString(chatRoomSummaryRes);
            chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + chatRoomSummaryRes.getId() + "/new", data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getOrLoadParticipantMap(Long roomId) {
        Map<String, String> participantMap = chatParticipantsRedisTemplate.opsForValue().get(PARTICIPANTS_KEY_PREFIX + roomId);

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

    private String toJson(Long roomId, Map<String, String> chatParticipantsMap) {
        List<ChatParticipantRes> chatParticipantResList = new ArrayList<>();

        for (Map.Entry<String, String> entry : chatParticipantsMap.entrySet()) {
            chatParticipantResList.add(
                    ChatParticipantRes.builder()
                            .email(entry.getKey())
                            .lastReadMessageId(Long.valueOf(entry.getValue()))
                            .roomId(roomId)
                            .build()
            );
        }

        String data = null;
        try {
            data = objectMapper.writeValueAsString(chatParticipantResList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    public void publishChatOnlineToRedis(Long roomId, ChatOnlineParticipantReq req) {
        log.info(req.getEmail());
        chatOnlineParticipantsRedisTemplate.opsForSet().add(ONLINE_KEY_PREFIX + roomId, req.getEmail());

        Set<String> onlineParticipantEmails = chatOnlineParticipantsRedisTemplate.opsForSet().members(ONLINE_KEY_PREFIX + roomId);

        List<ChatOnlineParticipantRes> chatOnlineParticipantResList = new ArrayList<>();
        if (onlineParticipantEmails != null) {
            onlineParticipantEmails.forEach(onlineParticipantEmail -> chatOnlineParticipantResList.add(ChatOnlineParticipantRes.builder().email(onlineParticipantEmail).build()));
        }

        try {
            String data = objectMapper.writeValueAsString(chatOnlineParticipantResList);
            chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", data);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for online participants: {}", e.getMessage());
            throw new RuntimeException("Error processing JSON for online participants");
        }
    }

    public void publishChatOfflineToRedis(Long roomId, ChatOnlineParticipantReq req) {
        chatOnlineParticipantsRedisTemplate.opsForSet().remove(ONLINE_KEY_PREFIX + roomId, req.getEmail());

        Set<String> onlineParticipantEmails = chatOnlineParticipantsRedisTemplate.opsForSet().members(ONLINE_KEY_PREFIX + roomId);

        List<ChatOnlineParticipantRes> chatOnlineParticipantResList = new ArrayList<>();
        if (onlineParticipantEmails != null) {
            onlineParticipantEmails.forEach(onlineParticipantEmail -> chatOnlineParticipantResList.add(ChatOnlineParticipantRes.builder().email(onlineParticipantEmail).build()));
        }

        try {
            String data = objectMapper.writeValueAsString(chatOnlineParticipantResList);
            chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", data);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for online participants: {}", e.getMessage());
            throw new RuntimeException("Error processing JSON for online participants");
        }
    }




    public void publishNewChatRoomToRedis(ChatRoomSummaryRes chatRoomSummaryRes) {
        try {
            String data = objectMapper.writeValueAsString(chatRoomSummaryRes);
            chatPubsubRedisTemplate.convertAndSend("/topic/chat-rooms/" + chatRoomSummaryRes.getId() + "/new", data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    // 레디스의 메세지를 받았을 때
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        String roomId = new String(message.getChannel()).split("/")[3];

        switch (channel) {
            case "/topic/chat-rooms/*/chat-message" -> {
                publishChatMessageToStompClient(roomId, message);
                sendMessageViaSse(roomId, message, "chat-message");
            }
            case "/topic/chat-rooms/*/chat-participants" -> publishChatParticipantsToStompClient(roomId, message);
            case "/topic/chat-rooms/*/chat-online-participants" -> publishChatOnlineParticipantsToStompClient(roomId, message);
            case "/topic/chat-rooms/*/new" -> sendMessageViaSse(roomId, message, "new-room");
        }
    }

    public void publishChatMessageToStompClient(String roomId, Message message) {
        log.info("start publishChatMessageToStompClient : {}", new String(message.getBody()));
        ChatMessageRes chatMessageRes = null;
        try {
            chatMessageRes = objectMapper.readValue(message.getBody(), ChatMessageRes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-message", chatMessageRes);
        log.info("end publishChatMessageToStompClient : {}", new String(message.getBody()));
    }

    public void publishChatParticipantsToStompClient(String roomId, Message message) {
        log.info("start publishChatParticipantsToStompClient : {}", new String(message.getBody()));
        List<ChatParticipantRes> chatParticipantResList = new ArrayList<>();
        try {
            JsonNode jsonNodes = objectMapper.readTree(message.getBody());
            for (JsonNode jsonNode : jsonNodes) {
                chatParticipantResList.add(objectMapper.readValue(jsonNode.toString(), ChatParticipantRes.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-participants", chatParticipantResList);
        log.info("end publishChatParticipantsToStompClient : {}", new String(message.getBody()));
    }

    public void publishChatOnlineParticipantsToStompClient(String roomId, Message message) {
        log.info("start publishChatOnlineParticipantsToStompClient : {}", new String(message.getBody()));
        List<ChatOnlineParticipantRes> chatOnlineParticipantResList = new ArrayList<>();
        try {
            JsonNode jsonNodes = objectMapper.readTree(message.getBody());
            for (JsonNode jsonNode : jsonNodes) {
                chatOnlineParticipantResList.add(objectMapper.readValue(jsonNode.toString(), ChatOnlineParticipantRes.class));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        messageTemplate.convertAndSend("/topic/chat-rooms/" + roomId + "/chat-online-participants", chatOnlineParticipantResList);
        log.info("end publishChatOnlineParticipantsToStompClient : {}", new String(message.getBody()));
    }

    public void sendMessageViaSse(String roomId, Message message, String name) {
        Set<String> emails = getOrLoadParticipantMap(Long.valueOf(roomId)).keySet();
        emails.forEach(email -> {
            SseEmitter emitter = sseEmitterRegistry.getEmitter(email);

            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name(name).data(message.getBody()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

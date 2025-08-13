package com.beyond.meongnyang.common.config;


import com.beyond.meongnyang.chat.service.ChatRedisService;
import com.beyond.meongnyang.common.service.SseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    @Qualifier("rtInventory")
    public RedisConnectionFactory refrshTokenRedisInventory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(0);  // refresh token 0번 db에 저장
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("rtInventory")
    public RedisTemplate<String, String> redisTemplate(@Qualifier("rtInventory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @Qualifier("chatFactory")
    public RedisConnectionFactory chatRedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(12);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("sseFactory")
    public RedisConnectionFactory ssePubSubConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        // red pub/sub기능은 db에 값을 저장하는 기능이 아니므로, 특정 db에 의존적이지 않음
        return new LettuceConnectionFactory(configuration);
    }


    @Bean
    @Qualifier("emailCodeInventory")
    public RedisConnectionFactory emailCodeRedisInventory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(1);  // refresh token 0번 db에 저장
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("chatPubSub")
    public RedisTemplate<String, String> chatPubSubRedisTemplate(@Qualifier("chatFactory") RedisConnectionFactory chatRedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(chatRedisConnectionFactory);
        return redisTemplate;
    }



    @Bean
    @Qualifier("chatParticipants")
    public RedisTemplate<String, Map<String, String>> chatParticipantsRedisTemplate(@Qualifier("chatFactory") RedisConnectionFactory chatRedisConnectionFactory) {
        RedisTemplate<String, Map<String, String>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(chatRedisConnectionFactory);

        // 모든 serializer를 String 기반으로 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Map.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));

        return redisTemplate;
    }

    @Bean
    @Qualifier("chatOnlineParticipants")
    public RedisTemplate<String, String> chatOnlineParticipantsRedisTemplate(@Qualifier("chatFactory") RedisConnectionFactory chatRedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(chatRedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    @Qualifier("ssePubSub")
    public RedisTemplate<String, String> ssePubSubTemplate(@Qualifier("sseFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    // redis pub/sub 리스너 객체
    @Bean
    @Qualifier("ssePusSub")
    public RedisMessageListenerContainer sseMessageListenerContainer(@Qualifier("sseFactory") RedisConnectionFactory connectionFactory,
                                                                     @Qualifier("sseListenerAdapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("ordering"));
        return container;
    }

    @Bean
    @Qualifier("chatMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(@Qualifier("chatFactory") RedisConnectionFactory connectionFactory,
                                                                       @Qualifier("chatListenerAdapter") MessageListenerAdapter chatListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatListenerAdapter, new PatternTopic("/topic/chat-rooms/*/chat-message"));
        container.addMessageListener(chatListenerAdapter, new PatternTopic("/topic/chat-rooms/*/chat-participants"));
        container.addMessageListener(chatListenerAdapter, new PatternTopic("/topic/chat-rooms/*/chat-online-participants"));
        return container;
    }

    @Bean
    @Qualifier("chatListenerAdapter")
    public MessageListenerAdapter chatListenerAdapter(ChatRedisService chatRedisService) {
        // 채널로 부터 수신되는 message 처리를 SseService 객체로 던져주고, SseAlarmService의 onMessage 메서드에서 처리한다.
        return new MessageListenerAdapter(chatRedisService, "onMessage");
    }

    @Bean
    @Qualifier("sseListenerAdapter")
    public MessageListenerAdapter sseListenerAdapter(SseService sseService) {
        // 채널로 부터 수신되는 message 처리를 SseService 객체로 던져주고, SseAlarmService의 onMessage 메서드에서 처리한다.
        return new MessageListenerAdapter(sseService, "onMessage");
    }

    @Bean  
    @Qualifier("emailCodeInventory")
    public RedisTemplate<String, String> emailCodeRedisTemplate(@Qualifier("emailCodeInventory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}

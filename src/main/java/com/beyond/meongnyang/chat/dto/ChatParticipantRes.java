package com.beyond.meongnyang.chat.dto;


import com.beyond.meongnyang.chat.entity.ChatParticipant;
import com.beyond.meongnyang.pet.entity.Pet;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatParticipantRes {
    private Long id;
    private String email;
    private Long roomId;
    private Long lastReadMessageId;
    private String profileImage;

    public static ChatParticipantRes fromEntity(ChatParticipant chatParticipant) {
        return ChatParticipantRes.builder()
                .id(chatParticipant.getId())
                .email(chatParticipant.getUser().getEmail())
                .profileImage(chatParticipant.getUser().getMainPet() == null ? "" : chatParticipant.getUser().getMainPet().getPetProfileUrl())
                .roomId(chatParticipant.getChatRoom().getId())
                .lastReadMessageId(chatParticipant.getLastReadMessage() == null ? 0 : chatParticipant.getLastReadMessage().getId())
                .build();
    }
}

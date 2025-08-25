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
        String profileImage = "";
        if (chatParticipant.getUser().getPets().stream().anyMatch(p -> p.getId().equals(chatParticipant.getUser().getMainPetId())))
            profileImage = chatParticipant.getUser().getPets().stream().filter(p -> p.getId().equals(chatParticipant.getUser().getMainPetId())).findFirst().get().getPetProfileUrl();
        return ChatParticipantRes.builder()
                .id(chatParticipant.getId())
                .email(chatParticipant.getUser().getEmail())
                .profileImage(profileImage)
                .roomId(chatParticipant.getChatRoom().getId())
                .lastReadMessageId(chatParticipant.getLastReadMessage() == null ? 0 : chatParticipant.getLastReadMessage().getId())
                .build();
    }
}

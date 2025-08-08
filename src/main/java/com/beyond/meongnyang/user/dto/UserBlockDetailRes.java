package com.beyond.meongnyang.user.dto;

import com.beyond.meongnyang.user.entity.UserBlock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserBlockDetailRes {
    private String userName;
    private String blockDate;

    public static UserBlockDetailRes fromEntity(UserBlock user){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 hh:mm:ss");
        return UserBlockDetailRes.builder()
                .userName(user.getBlockUser().getName())
                .blockDate(user.getCreatedAt().format(formatter))
                .build();
    }
}

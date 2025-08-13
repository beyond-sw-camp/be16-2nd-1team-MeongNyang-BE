package com.beyond.meongnyang.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SseMessageRes {
    private String sender;
    private String receiver;
    private String event;
    private String message;
}

package com.beyond.meongnyang.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerplexityReq {
    private String model;
    private List<Message> messages;
    private int max_tokens;
    private double temperature;
    private double top_p;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}

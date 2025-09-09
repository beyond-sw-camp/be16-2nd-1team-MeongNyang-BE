package com.beyond.meongnyang.common.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerplexityRes {
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Choice {
        private Message message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}



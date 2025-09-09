package com.beyond.meongnyang.post.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendHashTagRes {
    String tagName;
    Long tagCount;
}

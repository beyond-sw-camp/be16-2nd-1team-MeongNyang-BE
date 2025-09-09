package com.beyond.meongnyang.common.service;

import com.beyond.meongnyang.common.dto.PerplexityReq;
import com.beyond.meongnyang.common.dto.PerplexityRes;
import com.beyond.meongnyang.common.dto.PetTipReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class PerplexityService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${perplexity.api-key}")
    private String apiKey;

    @Value("${perplexity.api-url}")
    private String apiUrl;

    public PerplexityRes getPetTip(PetTipReq res) {
        String contextPrompt = "";
        if (!res.getWeather().isBlank() && !res.getTemperature().isBlank()) {
            contextPrompt = "현재 날씨: " + res.getWeather() + ", 온도: " + res.getTemperature() + "°C. ";
        }
        if (!res.getPetType().isEmpty()) {
            contextPrompt += "반려동물 종류: " + res.getPetType() + ". ";
        }

        String prompt = contextPrompt + "오늘 날씨와 상황에 맞는 반려동물 돌봄 팁을 한국어로 제공해주세요. 다음 조건을 만족하는 창의적이고 실용적인 조언을 해주세요: " +
                "- 일반적인 상식이 아닌 전문적이고 구체적인 팁 - 실제로 많은 사람들이 놓치기 쉬운 중요한 포인트 - 날씨나 상황에 특화된 맞춤형 조언 " +
                "- 구체적인 방법이나 기법 제시 - 반려동물의 행동이나 건강에 미치는 영향 설명";

        PerplexityReq.Message systemMessage = new PerplexityReq.Message();
        systemMessage.setRole("system");
        systemMessage.setContent("당신은 20년 경력의 반려동물 전문가이자 수의사입니다. 일반적인 상식이 아닌 전문적이고 실용적인 조언을 제공합니다. 많은 사람들이 놓치기 쉬운 중요한 포인트나 전문가만이 알 수 있는 구체적인 방법을 제시해주세요. 참조 번호나 인용 표시 없이 자연스러운 문장으로만 답변해주세요.");

        PerplexityReq.Message userMessage = new PerplexityReq.Message();
        userMessage.setRole("user");
        userMessage.setContent(prompt);

        PerplexityReq req = new PerplexityReq();
        req.setModel("sonar");
        req.setMessages(Arrays.asList(systemMessage, userMessage));
        req.setMax_tokens(250);
        req.setTemperature(0.8);
        req.setTop_p(0.9);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<PerplexityReq> entity = new HttpEntity<>(req, headers);

        ResponseEntity<PerplexityRes> perplexityRes = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, PerplexityRes.class);

        if (perplexityRes.getStatusCode() == HttpStatus.OK && perplexityRes.getBody() != null) {
            return perplexityRes.getBody();
        } else {
            throw new RuntimeException("Perplexity API 호출 실패: " + perplexityRes.getStatusCode());
        }
    }
}

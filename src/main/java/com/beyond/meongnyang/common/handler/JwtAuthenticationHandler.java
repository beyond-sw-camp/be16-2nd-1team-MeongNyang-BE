package com.beyond.meongnyang.common.handler;

import com.beyond.meongnyang.common.dto.CommonRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class JwtAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        CommonRes<Object> commonRes = CommonRes.ofFailure(HttpStatus.UNAUTHORIZED.value(), "token이 없거나 유효하지 않습니다.");

        ObjectMapper objectMapper = new ObjectMapper();
        String body  = objectMapper.writeValueAsString(commonRes);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(body);
    }
}

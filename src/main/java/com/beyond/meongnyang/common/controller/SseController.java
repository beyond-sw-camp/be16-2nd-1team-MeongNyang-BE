package com.beyond.meongnyang.common.controller;

import com.beyond.meongnyang.common.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping("")
    public SseEmitter connect() {
        return this.sseService.connect();
    }

    @DeleteMapping("")
    public void disconnect() {
        this.sseService.disconnect();
    }

}
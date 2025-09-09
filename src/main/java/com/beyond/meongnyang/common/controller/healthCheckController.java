package com.beyond.meongnyang.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthCheckController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}

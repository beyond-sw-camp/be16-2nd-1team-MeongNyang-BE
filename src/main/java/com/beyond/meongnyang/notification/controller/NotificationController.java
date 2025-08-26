package com.beyond.meongnyang.notification.controller;

import com.beyond.meongnyang.notification.service.NotificationService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    public final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(CommonRes.ofSuccess(notificationService.findMyAlarms(), HttpStatus.OK.value(), "alarms found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        notificationService.deleteById(id);
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarm deleted"));
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteMyAlarms() {
        notificationService.deleteMyAlarms();
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarms deleted"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> readById(@PathVariable Long id) {
        notificationService.readById(id);
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarm read"));
    }

    @PatchMapping("")
    public ResponseEntity<?> readMyAlarms() {
        notificationService.readMyAlarms();
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarms read"));
    }
}

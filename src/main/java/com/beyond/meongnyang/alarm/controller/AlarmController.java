package com.beyond.meongnyang.alarm.controller;

import com.beyond.meongnyang.alarm.service.AlarmService;
import com.beyond.meongnyang.common.dto.CommonRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/alarms")
@RequiredArgsConstructor
public class AlarmController {
    public final AlarmService alarmService;

    @GetMapping("")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(CommonRes.ofSuccess(alarmService.findMyAlarms(), HttpStatus.OK.value(), "alarms found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        alarmService.deleteById(id);
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarm deleted"));
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteMyAlarms() {
        alarmService.deleteMyAlarms();
        return ResponseEntity.ok(CommonRes.ofSuccess(null, HttpStatus.OK.value(), "alarms deleted"));

    }
}

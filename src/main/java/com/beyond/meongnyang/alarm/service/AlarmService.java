package com.beyond.meongnyang.alarm.service;

import com.beyond.meongnyang.alarm.dto.AlarmRes;
import com.beyond.meongnyang.alarm.entity.Alarm;
import com.beyond.meongnyang.alarm.repository.AlarmRepository;
import com.beyond.meongnyang.common.domain.Bool;
import com.beyond.meongnyang.common.service.CommonService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final CommonService commonService;

    public void create(Alarm alarm) {
        alarmRepository.save(alarm);
    }

    public List<AlarmRes> findMyAlarms() {
        return alarmRepository.findAllByReceiver(commonService.getCurrentUser()).stream().map(AlarmRes::fromEntity).toList();
    }

    public void deleteById(Long id) {
        Alarm alarm = alarmRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("alarm not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!alarm.getReceiver().getEmail().equals(email)) throw new AccessDeniedException("Not your alarm");

        alarmRepository.delete(alarm);
    }

    public void deleteMyAlarms() {
        alarmRepository.deleteAllByReceiver(commonService.getCurrentUser());
    }

    public void readById(Long id) {
        Alarm alarm = alarmRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("alarm not found"));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!alarm.getReceiver().getEmail().equals(email)) throw new AccessDeniedException("Not your alarm");

        alarm.read();
    }

    public void readMyAlarms() {
        alarmRepository.findAllByReceiverAndIsRead(commonService.getCurrentUser(), Bool.FALSE).forEach(Alarm::read);
    }
}

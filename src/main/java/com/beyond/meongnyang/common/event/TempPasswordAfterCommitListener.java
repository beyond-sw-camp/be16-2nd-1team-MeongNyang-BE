package com.beyond.meongnyang.common.event;

import com.beyond.meongnyang.user.service.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TempPasswordAfterCommitListener {

    private final SendEmailService sendEmailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(TempPasswordIssuedEvent e) {
        // 트랜잭션 커밋 이후에만 호출됨
        System.out.println("[DEBUG] AFTER_COMMIT fired for " + e.to());
        sendEmailService.sendTemporaryPassword(e.to(), e.tempPassword());
    }
}

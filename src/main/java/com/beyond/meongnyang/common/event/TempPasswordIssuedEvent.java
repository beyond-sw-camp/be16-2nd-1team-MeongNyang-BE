package com.beyond.meongnyang.common.event;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public record TempPasswordIssuedEvent(String to, String tempPassword) {
}

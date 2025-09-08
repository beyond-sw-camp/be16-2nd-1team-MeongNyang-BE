package com.beyond.meongnyang.market.repository;

import com.beyond.meongnyang.market.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}

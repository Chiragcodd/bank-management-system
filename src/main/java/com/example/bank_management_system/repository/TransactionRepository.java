package com.example.bank_management_system.repository;

import com.example.bank_management_system.entity.Transaction;
import com.example.bank_management_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccountOrderByDateTimeDesc(Account account );
    List<Transaction> findByAccountIdOrderByDateTimeDesc(Long accountId);
}


package com.example.bank_management_system.service;

import com.example.bank_management_system.entity.Transaction;
import com.example.bank_management_system.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository t) {
        this.transactionRepository = t;
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByDateTimeDesc(accountId);
    }
}

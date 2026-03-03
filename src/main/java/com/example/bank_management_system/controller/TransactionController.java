package com.example.bank_management_system.controller;

import com.example.bank_management_system.entity.Transaction;
import com.example.bank_management_system.dto.*;
import com.example.bank_management_system.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}")
    public ApiResponse<List<TransactionDto>> getTransactions(@PathVariable Long accountId) {

        List<Transaction> list = transactionService.getTransactionsByAccountId(accountId);
        List<TransactionDto> dtoList = list.stream()
            .map(tx -> new TransactionDto(
                    tx.getType() != null ? tx.getType().name() : "UNKNOWN",
                    tx.getAmount(),
                    tx.getDateTime(),
                    tx.getStatus() != null ? tx.getStatus().name() : "PENDING"
            ))
            .collect(Collectors.toList());
        return new ApiResponse<>(true, "Transaction history", dtoList);
    }
}

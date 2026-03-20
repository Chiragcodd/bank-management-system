package com.example.bank_management_system.controller;

import com.example.bank_management_system.dto.ApiResponse;
import com.example.bank_management_system.dto.TransactionDto;
import com.example.bank_management_system.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/my")
    public ApiResponse<List<TransactionDto>> getMyTransactions(Authentication authentication) {

        String username = authentication.getName();
        List<TransactionDto> transactions = transactionService.getMyTransactions(username);
        return new ApiResponse<>(true, "Transaction history", transactions);
    }

    @GetMapping("/admin/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TransactionDto>> getTransactionsByAccountId(
            @PathVariable Long accountId) {

        List<TransactionDto> transactions =
                transactionService.getTransactionsByAccountId(accountId);
        return new ApiResponse<>(true, "Transaction history", transactions);
    }
}
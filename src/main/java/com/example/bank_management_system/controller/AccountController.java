package com.example.bank_management_system.controller;

import com.example.bank_management_system.dto.*;
import com.example.bank_management_system.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ApiResponse<AccountResponseDto> getMyAccount(Authentication authentication) {

        String username = authentication.getName();
        AccountResponseDto account = accountService.getAccountByUsername(username);
        return new ApiResponse<>(true, "Account fetched", account);
    }

    @PostMapping("/deposit")
    public ApiResponse<AccountResponseDto> deposit(
            Authentication authentication,
            @Valid @RequestBody TransactionRequest request) {

        String username = authentication.getName();
        AccountResponseDto account = accountService.depositByUsername(username, request.getAmount());
        return new ApiResponse<>(true, "Amount deposited successfully", account);
    }

    @PostMapping("/withdraw")
    public ApiResponse<AccountResponseDto> withdraw(
            Authentication authentication,
            @Valid@RequestBody TransactionRequest request) {

        String username = authentication.getName();
        AccountResponseDto account = accountService.withdrawByUsername(username, request.getAmount());
        return new ApiResponse<>(true, "Amount withdrawn", account);
    }

    @PostMapping("/transfer")
    public ApiResponse<String> transfer(
            Authentication authentication,
            @Valid @RequestBody TransferRequest request) {

        String username = authentication.getName();
        String result = accountService.transferByUsername(
                username, request.getToAccountNumber(), request.getAmount());
        return new ApiResponse<>(true, result, null);
    }

    @GetMapping("/transactions")
    public ApiResponse<List<TransactionDto>> getMyTransactions(Authentication authentication){

        String username = authentication.getName();
        List<TransactionDto> transactions = accountService.getMyTransactions(username);
        return new ApiResponse<>(true,"Transaction history", transactions);
    }   
}
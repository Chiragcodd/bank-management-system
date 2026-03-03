package com.example.bank_management_system.controller;

import com.example.bank_management_system.dto.*;
import com.example.bank_management_system.entity.Account;
import com.example.bank_management_system.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{userId}")
    public ApiResponse<AccountResponseDto> getAccount(@PathVariable Long userId) {
        Account account = accountService.getAccountByUserId(userId);
        AccountResponseDto dto = new AccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType().name()
        );
        return new ApiResponse<>(true, "Account fetched", dto);
    }

    @PostMapping("/deposit")
    public ApiResponse<AccountResponseDto> deposit(@Valid @RequestBody TransactionRequest request) {
        Account account = accountService.deposit(request.getUserId(), request.getAmount());
        AccountResponseDto dto = new AccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType().name()
        );
        return new ApiResponse<>(true, "Amount deposited", dto);
    }

    @PostMapping("/withdraw")
    public ApiResponse<AccountResponseDto> withdraw(@Valid @RequestBody TransactionRequest request) {
        Account account = accountService.withdraw(request.getUserId(), request.getAmount());
        AccountResponseDto dto = new AccountResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType().name()
        );
        return new ApiResponse<>(true, "Amount withdrawn", dto);
    }
}

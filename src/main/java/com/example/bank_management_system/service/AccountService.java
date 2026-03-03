package com.example.bank_management_system.service;

import com.example.bank_management_system.entity.*;
import com.example.bank_management_system.entity.enums.*;
import com.example.bank_management_system.repository.*;
import com.example.bank_management_system.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository a, TransactionRepository t, UserRepository u) {
        this.accountRepository = a;
        this.transactionRepository = t;
        this.userRepository = u;
    }

    public Account getAccountByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));
        return accountRepository.findByUser(user)
                .orElseThrow(() -> new CustomException("Account not found"));
    }

    @Transactional
    public Account deposit(Long userId, double amount) {
        if (amount <= 0) throw new CustomException("Invalid amount");
        Account account = getAccountByUserId(userId);
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType(TransactionType.DEPOSIT);
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setDateTime(LocalDateTime.now());
        transactionRepository.save(tx);

        return account;
    }

    @Transactional
    public Account withdraw(Long userId, double amount) {
        if (amount <= 0) throw new CustomException("Invalid amount");
        Account account = getAccountByUserId(userId);
        if (account.getBalance() < amount)
            throw new CustomException("Insufficient balance");

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType(TransactionType.WITHDRAW);
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setDateTime(LocalDateTime.now());
        transactionRepository.save(tx);

        return account;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}

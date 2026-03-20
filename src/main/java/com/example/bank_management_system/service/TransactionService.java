package com.example.bank_management_system.service;

import com.example.bank_management_system.dto.TransactionDto;
import com.example.bank_management_system.entity.Account;
import com.example.bank_management_system.entity.Transaction;
import com.example.bank_management_system.exception.CustomException;
import com.example.bank_management_system.repository.AccountRepository;
import com.example.bank_management_system.repository.TransactionRepository;
import com.example.bank_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                                AccountRepository accountRepository,
                                UserRepository userRepository ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionDto> getMyTransactions(String username) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not Found"));

        var accounts = accountRepository.findByUserId(user.getId());
        if (accounts.isEmpty())
                throw new CustomException("No account found");

        Account account = accounts.get(0);

        List<Transaction> transactions = transactionRepository.findByAccountOrderByDateTimeDesc(account);

        return transactions.stream()
                .map(tx -> new TransactionDto(
                        tx.getType(),
                        tx.getAmount(),
                        tx.getDateTime(),
                        tx.getStatus(),
                        tx.getCounterpartyAccount(),
                        tx.getDirection()
                ))
                .collect(Collectors.toList());
    }

     public List<TransactionDto> getTransactionsByAccountId(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new CustomException("Account not found"));

        return transactionRepository
                .findByAccountOrderByDateTimeDesc(account)
                .stream()
                .map(tx -> new TransactionDto(
                        tx.getType(),
                        tx.getAmount(),
                        tx.getDateTime(),
                        tx.getStatus(),
                        tx.getCounterpartyAccount(),
                        tx.getDirection()
                ))
                .collect(Collectors.toList());
    }
}
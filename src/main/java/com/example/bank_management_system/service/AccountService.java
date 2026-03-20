package com.example.bank_management_system.service;

import com.example.bank_management_system.dto.AccountResponseDto;
import com.example.bank_management_system.dto.TransactionDto;
import com.example.bank_management_system.entity.*;
import com.example.bank_management_system.entity.enums.TransactionStatus;
import com.example.bank_management_system.entity.enums.TransactionType;
import com.example.bank_management_system.exception.CustomException;
import com.example.bank_management_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDto getAccountByUsername(String username) {
        return mapToDto(findAccountByUsername(username));
    }

    @Transactional
    public AccountResponseDto depositByUsername(String username, BigDecimal amount) {
        // ✅ FIX: BigDecimal comparison
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomException("Amount must be greater than 0");

        Account account = findAccountByUsername(username);
        // ✅ FIX: BigDecimal.add() — no floating point error
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        saveTransaction(account, amount, TransactionType.DEPOSIT, TransactionStatus.SUCCESS, null, null);
        return mapToDto(account);
    }

    @Transactional
    public AccountResponseDto withdrawByUsername(String username, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomException("Amount must be greater than 0");

        Account account = findAccountByUsername(username);

        if (account.getBalance().compareTo(amount) < 0)
            throw new CustomException("Insufficient balance");

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        saveTransaction(account, amount, TransactionType.WITHDRAW, TransactionStatus.SUCCESS, null, null);
        return mapToDto(account);
    }

    @Transactional
    public String transferByUsername(String senderUsername, String toAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomException("Amount must be greater than 0");

        Account senderAccount = findAccountByUsername(senderUsername);

        if (senderAccount.getAccountNumber().equals(toAccountNumber))
            throw new CustomException("Cannot transfer to your own account");

        Account receiverAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new CustomException("Receiver account not found"));

        if (senderAccount.getBalance().compareTo(amount) < 0)
            throw new CustomException("Insufficient balance");

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        saveTransaction(senderAccount, amount, TransactionType.TRANSFER,
                TransactionStatus.SUCCESS, receiverAccount.getAccountNumber(), "SENT");
        saveTransaction(receiverAccount, amount, TransactionType.TRANSFER,
                TransactionStatus.SUCCESS, senderAccount.getAccountNumber(), "RECEIVED");

        return "Transfer successful";
    }

    public List<TransactionDto> getMyTransactions(String username) {
        Account account = findAccountByUsername(username);
        return transactionRepository
                .findByAccountOrderByDateTimeDesc(account)
                .stream()
                .map(tx -> new TransactionDto(
                        tx.getType(), tx.getAmount(), tx.getDateTime(),
                        tx.getStatus(), tx.getCounterpartyAccount(), tx.getDirection()))
                .collect(Collectors.toList());
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    Account findAccountByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        if (accounts.isEmpty())
            throw new CustomException("No account found for this user");
        return accounts.get(0);
    }

    private void saveTransaction(Account account, BigDecimal amount, TransactionType type,
                                  TransactionStatus status, String counterpartyAccount, String direction) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setStatus(status);
        tx.setDateTime(LocalDateTime.now());
        tx.setCounterpartyAccount(counterpartyAccount);
        tx.setDirection(direction);
        transactionRepository.save(tx);
    }

    private AccountResponseDto mapToDto(Account account) {
        return new AccountResponseDto(
                account.getId(), account.getAccountNumber(),
                account.getBalance(), account.getAccountType());
    }
}
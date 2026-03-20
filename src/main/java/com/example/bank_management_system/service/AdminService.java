package com.example.bank_management_system.service;

import com.example.bank_management_system.dto.AdminAccountResponseDto;
import com.example.bank_management_system.entity.Account;
import com.example.bank_management_system.entity.User;
import com.example.bank_management_system.exception.CustomException;
import com.example.bank_management_system.repository.AccountRepository;
import com.example.bank_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AdminService(AccountRepository accountRepository,
                        UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<AdminAccountResponseDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToAdminDto)
                .collect(Collectors.toList());
    }

    public List<AdminAccountResponseDto> searchAccountByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));

        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToAdminDto)
                .collect(Collectors.toList());
    }

    private AdminAccountResponseDto mapToAdminDto(Account acc) {
        return new AdminAccountResponseDto(
                acc.getId(),
                acc.getAccountNumber(),
                acc.getBalance(),
                acc.getAccountType().name(),
                acc.getUser().getUsername(),
                acc.getUser().getFullName(),
                acc.getUser().getEmail(),
                acc.getUser().getMobileNumber()
        );
    }
}
package com.example.bank_management_system.dto;

import com.example.bank_management_system.entity.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
}
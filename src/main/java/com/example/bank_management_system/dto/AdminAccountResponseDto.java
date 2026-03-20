package com.example.bank_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class AdminAccountResponseDto {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;

    private String username;
    private String fullName;
    private String email;
    private String mobileNumber;
}


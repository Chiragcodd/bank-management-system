package com.example.bank_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private double balance;
    private String accountType;

}
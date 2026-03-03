package com.example.bank_management_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;
}


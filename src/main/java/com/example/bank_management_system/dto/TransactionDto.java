package com.example.bank_management_system.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionDto {

    private String type;
    private double amount;
    private LocalDateTime dateTime;
    private String status;

}

package com.example.bank_management_system.dto;

import com.example.bank_management_system.entity.enums.TransactionStatus;
import com.example.bank_management_system.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private TransactionStatus status;
    private String counterpartyAccount;
    private String direction;
}

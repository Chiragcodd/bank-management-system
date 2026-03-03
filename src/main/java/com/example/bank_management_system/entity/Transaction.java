package com.example.bank_management_system.entity;

import com.example.bank_management_system.entity.enums.TransactionType;
import com.example.bank_management_system.entity.enums.TransactionStatus;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    // private String type; // deposit / withdraw / transfer
    private double amount;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}



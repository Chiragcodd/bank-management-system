package com.example.bank_management_system.entity;

import com.example.bank_management_system.entity.enums.TransactionType;
import com.example.bank_management_system.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;


    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "counterparty_account")
    private String counterpartyAccount;

    @Column(name = "direction")
    private String direction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}



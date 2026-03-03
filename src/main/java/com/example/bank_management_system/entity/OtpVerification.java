package com.example.bank_management_system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_verification")
public class OtpVerification {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile_number") 
    private String mobileNumber;

    private String otp;
    private LocalDateTime expiryTime;
    private boolean used = false;
}

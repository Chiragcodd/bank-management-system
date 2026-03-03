package com.example.bank_management_system.repository;

import com.example.bank_management_system.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByMobileNumberAndOtpAndUsedFalseOrderByExpiryTimeDesc(String mobileNumber, String otp);
}

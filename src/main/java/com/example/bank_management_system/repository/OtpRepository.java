package com.example.bank_management_system.repository;

import org.springframework.stereotype.Repository;
import com.example.bank_management_system.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByMobileNumberAndOtpAndUsedFalseOrderByIdDesc(String mobileNumber, String otp);

    void deleteByExpiryTimeBefore(LocalDateTime now);
    boolean existsByMobileNumberAndUsedFalseAndExpiryTimeAfter(String mobileNumber, LocalDateTime now);
}
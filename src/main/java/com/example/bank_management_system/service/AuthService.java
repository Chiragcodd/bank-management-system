package com.example.bank_management_system.service;

import com.example.bank_management_system.dto.SignupRequest;
import com.example.bank_management_system.entity.*;
import com.example.bank_management_system.entity.enums.Role;
import com.example.bank_management_system.repository.*;
import com.example.bank_management_system.exception.CustomException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository u, AccountRepository a, OtpRepository o, PasswordEncoder pe) {
        this.userRepository = u;
        this.accountRepository = a;
        this.otpRepository = o;
        this.passwordEncoder = pe;
    }

    public String signup(SignupRequest request) {

    if (userRepository.existsByUsername(request.getUsername()))  
        throw new CustomException("Username already exists");

        User user = new User();
        user.setUsername(request.getUsername());
        // user.setPassword(request.getPassword());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());

        user.setVerified(false);
        user.setRole(Role.USER);
        User saved = userRepository.save(user);

        Account account = new Account();
        account.setUser(saved);
        account.setAccountType(request.getAccountType());
        account.setBalance(0);
        account.setAccountNumber(String.valueOf(100000000000L + new Random().nextLong(900000000000L)));
        accountRepository.save(account);

        String otp = String.format("%04d", new Random().nextInt(10000));
        OtpVerification ov = new OtpVerification();
        ov.setMobileNumber(user.getMobileNumber());
        ov.setOtp(otp);
        ov.setExpiryTime(LocalDateTime.now().plusMinutes(1));
        ov.setUsed(false);
        otpRepository.save(ov);

        System.out.println("OTP = " + otp);
        return "Signup successful, OTP sent";
    }

    public String verifyOtp(String mobile, String otp) {
        OtpVerification ov = otpRepository
                .findByMobileNumberAndOtpAndUsedFalseOrderByExpiryTimeDesc(mobile, otp)
                .orElseThrow(() -> new CustomException("Invalid OTP"));

        if (ov.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new CustomException("OTP expired");

        ov.setUsed(true);
        otpRepository.save(ov);

        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
        return "Mobile verified successfully";
    }

        // ✅ NEW METHOD — RESEND OTP
    public String resendOtp(String mobile) {

        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new CustomException("User not found"));

        String newOtp = String.format("%04d", new Random().nextInt(10000));

        OtpVerification ov = new OtpVerification();
        ov.setMobileNumber(mobile);
        ov.setOtp(newOtp);
        ov.setExpiryTime(LocalDateTime.now().plusMinutes(1)); // 1 minute valid
        ov.setUsed(false);

        otpRepository.save(ov);

        System.out.println("NEW RESENT OTP = " + newOtp);
        return "New OTP sent successfully";
    }


    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Invalid username"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException("Wrong password");

        if (!user.isVerified())
            throw new CustomException("Please verify OTP first");

        return user;
    }
}
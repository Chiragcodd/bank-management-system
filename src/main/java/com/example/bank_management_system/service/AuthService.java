package com.example.bank_management_system.service;

import com.example.bank_management_system.dto.*;
import com.example.bank_management_system.entity.*;
import com.example.bank_management_system.entity.enums.Role;
import com.example.bank_management_system.repository.*;
import com.example.bank_management_system.security.JwtUtil;
import com.example.bank_management_system.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // ✅ FIX: Random → SecureRandom (OTP prediction attack se bachao)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository u, AccountRepository a, OtpRepository o,
                       PasswordEncoder pe, JwtUtil j) {
        this.userRepository = u;
        this.accountRepository = a;
        this.otpRepository = o;
        this.passwordEncoder = pe;
        this.jwtUtil = j;
    }

    // ✅ FIX: @Transactional add kiya
    @Transactional
    public String signup(SignupRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            throw new CustomException("Username already exists");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new CustomException("Email already exists");

        if (userRepository.existsByMobileNumber(request.getMobileNumber()))
            throw new CustomException("Mobile number already registered");

        User user = new User();
        user.setUsername(request.getUsername());
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
        // ✅ FIX: 0.0 → BigDecimal.ZERO
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateUniqueAccountNumber());
        accountRepository.save(account);

        generateAndPrintOtp(saved.getMobileNumber());

        return "Signup successful. Check server terminal for OTP.";
    }

    @Transactional
    public String verifyOtp(String mobile, String otp) {

        OtpVerification ov = otpRepository
                .findTopByMobileNumberAndOtpAndUsedFalseOrderByIdDesc(mobile, otp)
                .orElseThrow(() -> new CustomException("Invalid OTP"));

        if (ov.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new CustomException("OTP expired. Please request a new one.");

        ov.setUsed(true);
        otpRepository.save(ov);

        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        return "Mobile verified successfully";
    }

    @Transactional
    public String resendOtp(String mobile) {

        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new CustomException("User not found"));

        if (user.isVerified())
            throw new CustomException("User is already verified");

        if (otpRepository.existsByMobileNumberAndUsedFalseAndExpiryTimeAfter(
                mobile, LocalDateTime.now()))
            throw new CustomException("OTP already sent. Please wait before requesting again.");

        generateAndPrintOtp(mobile);

        return "New OTP generated. Check server terminal.";
    }

    public LoginResponse login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException("Invalid username or password");

        if (!user.isVerified()) {
            return new LoginResponse(
                    user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.getMobileNumber(), false,
                    user.getRole().name(), null
            );
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(
                user.getId(), user.getUsername(), user.getFullName(),
                user.getEmail(), user.getMobileNumber(), true,
                user.getRole().name(), token
        );
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void generateAndPrintOtp(String mobile) {
        // ✅ FIX: SecureRandom use ho raha hai
        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));

        OtpVerification ov = new OtpVerification();
        ov.setMobileNumber(mobile);
        ov.setOtp(otp);
        ov.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        ov.setUsed(false);
        otpRepository.save(ov);

        // ✅ Terminal mein clearly box format mein dikhega
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           OTP GENERATED              ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  Mobile : " + mobile + "          ║");
        System.out.println("║  OTP    : " + otp + "                    ║");
        System.out.println("║  Valid  : 5 minutes                  ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        log.info("OTP generated for mobile: {}", mobile);
    }

    private String generateUniqueAccountNumber() {
        // ✅ FIX: Loop mein duplicate check — unique account number guaranteed
        String accountNumber;
        do {
            long num = 100_000_000_000L + (long) (SECURE_RANDOM.nextDouble() * 900_000_000_000L);
            accountNumber = String.valueOf(num);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
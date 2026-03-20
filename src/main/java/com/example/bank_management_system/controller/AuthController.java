package com.example.bank_management_system.controller;

import com.example.bank_management_system.dto.*;
import com.example.bank_management_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupRequest request) {
        String msg = authService.signup(request);
        return new ApiResponse<>(true, msg, null);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@Valid @RequestBody OtpRequest request) {
        String msg = authService.verifyOtp(request.getMobileNumber(), request.getOtp());
        return new ApiResponse<>(true, msg, null);
    }

    @PostMapping("/resend-otp")
    public ApiResponse<String> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        String msg = authService.resendOtp(request.getMobileNumber());
        return new ApiResponse<>(true, msg, null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response =
                authService.login(request.getUsername(), request.getPassword());

        return new ApiResponse<>(true, "Login successful", response);
    }
}

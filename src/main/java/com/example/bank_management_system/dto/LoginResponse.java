package com.example.bank_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String mobileNumber;
    private boolean verified;
}


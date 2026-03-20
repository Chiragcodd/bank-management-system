package com.example.bank_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendOtpRequest {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobileNumber;
}

package com.example.bank_management_system.controller;

import com.example.bank_management_system.dto.AdminAccountResponseDto;
import com.example.bank_management_system.dto.ApiResponse;
import com.example.bank_management_system.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/all-accounts")
    public ApiResponse<List<AdminAccountResponseDto>> getAllAccounts() {
        return new ApiResponse<>(true, "All accounts", adminService.getAllAccounts());
    }

    @GetMapping("/search")
    public ApiResponse<List<AdminAccountResponseDto>> searchByUsername(
            @RequestParam String username) {
        return new ApiResponse<>(true, "Search result",
                adminService.searchAccountByUsername(username));
    }
}
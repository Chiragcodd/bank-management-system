package com.example.bank_management_system.entity;

import com.example.bank_management_system.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String fullName;
    private String email;

    @Column(name = "mobile_number")
    private String mobileNumber;
    
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}

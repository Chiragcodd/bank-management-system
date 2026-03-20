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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "mobile_number", unique = true, nullable = false)
    private String mobileNumber;

    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
}
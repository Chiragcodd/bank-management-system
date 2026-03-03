package com.example.bank_management_system.repository;

import com.example.bank_management_system.entity.Account;
import com.example.bank_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUser(User user);// ✅ findByUser instead of findByUserId
}
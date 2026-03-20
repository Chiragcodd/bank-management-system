package com.example.bank_management_system.repository;

import com.example.bank_management_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);
    
    Optional<Account>findByAccountNumber(String accountNumber); 
    boolean existsByAccountNumber(String accountNumber);

}
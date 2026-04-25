package com.bank.Hackathon_Java6.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.Hackathon_Java6.Entity.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Integer> {
}

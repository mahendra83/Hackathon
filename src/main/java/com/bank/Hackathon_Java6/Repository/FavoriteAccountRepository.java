package com.bank.Hackathon_Java6.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.Hackathon_Java6.Entity.FavoriteAccount;

@Repository
public interface FavoriteAccountRepository extends JpaRepository<FavoriteAccount, Integer> {
    Page<FavoriteAccount> findByCustomer_CustomerId(Integer customerId, Pageable pageable);
    Optional<FavoriteAccount> findByAccountIdAndCustomer_CustomerId(Integer accountId, Integer customerId);
    long countByCustomer_CustomerId(Integer customerId);
}
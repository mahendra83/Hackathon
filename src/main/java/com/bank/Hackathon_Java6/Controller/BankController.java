package com.bank.Hackathon_Java6.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.Hackathon_Java6.Entity.Bank;
import com.bank.Hackathon_Java6.Exception.BankNotFoundException;
import com.bank.Hackathon_Java6.Repository.BankRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankRepository bankRepository;

    @GetMapping("/{bankCode}")
    public ResponseEntity<Map<String, Object>> getBankByCode(@PathVariable Integer bankCode) {
        Bank bank = bankRepository.findById(bankCode)
                .orElseThrow(() -> new BankNotFoundException(bankCode));

        return ResponseEntity.ok(Map.of(
                "bankCode", bank.getBankCode(),
                "bankName", bank.getBankName()));
    }
}

package com.bank.Hackathon_Java6.Exception;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Integer bankCode) {
        super("Bank not found with code: " + bankCode);
    }
}
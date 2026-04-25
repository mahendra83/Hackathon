package com.bank.Hackathon_Java6.Exception;

public class DuplicateIbanException extends RuntimeException {
    public DuplicateIbanException(String message) {
        super(message);
    }
}


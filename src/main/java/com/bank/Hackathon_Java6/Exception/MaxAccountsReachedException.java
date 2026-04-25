package com.bank.Hackathon_Java6.Exception;


public class MaxAccountsReachedException extends RuntimeException {
    public MaxAccountsReachedException(String message) {
        super(message);
    }
}
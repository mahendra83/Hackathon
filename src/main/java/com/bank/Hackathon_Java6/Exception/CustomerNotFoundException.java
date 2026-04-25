package com.bank.Hackathon_Java6.Exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Integer customerId) {
        super("Customer not found with ID: " + customerId);
    }
}

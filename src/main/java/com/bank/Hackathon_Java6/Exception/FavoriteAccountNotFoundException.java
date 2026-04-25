package com.bank.Hackathon_Java6.Exception;

public class FavoriteAccountNotFoundException extends RuntimeException {
    public FavoriteAccountNotFoundException(Integer accountId) {
        super("Favorite account not found with ID: " + accountId);
    }
    public FavoriteAccountNotFoundException(Integer accountId, Integer customerId) {
        super("Favorite account " + accountId + " not found for customer " + customerId);
    }
}


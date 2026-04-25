package com.bank.Hackathon_Java6.Exception;

public class MaxFavoriteAccountsExceededException extends RuntimeException {
    public MaxFavoriteAccountsExceededException() {
        super("Maximum limit of 20 favorite accounts reached. Cannot add more.");
    }
}
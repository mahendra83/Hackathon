package com.bank.Hackathon_Java6.Exception;

import org.springframework.http.HttpStatus;

public class LoginRateLimitExceededException extends RuntimeException {

    private final HttpStatus status;

    public LoginRateLimitExceededException() {
        super("Too many failed login attempts. Try again after 5 minutes.");
        this.status = HttpStatus.TOO_MANY_REQUESTS;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

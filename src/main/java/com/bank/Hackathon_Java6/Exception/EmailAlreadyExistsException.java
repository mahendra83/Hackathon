package com.bank.Hackathon_Java6.Exception;


import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends RuntimeException {

    private final HttpStatus status;

    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
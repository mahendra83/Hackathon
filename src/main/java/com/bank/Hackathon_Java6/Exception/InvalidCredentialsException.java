package com.bank.Hackathon_Java6.Exception;


import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends RuntimeException {

    private final HttpStatus status;

    public InvalidCredentialsException() {
        super("CustomerId or Password is incorrect");
        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
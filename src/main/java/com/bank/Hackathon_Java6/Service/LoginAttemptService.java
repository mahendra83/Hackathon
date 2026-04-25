package com.bank.Hackathon_Java6.Service;

public interface LoginAttemptService {

    void validateAttemptAllowed(Integer customerId);

    void recordFailedAttempt(Integer customerId);

    void recordSuccessfulAttempt(Integer customerId);
}

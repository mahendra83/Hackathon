package com.bank.Hackathon_Java6.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

import com.bank.Hackathon_Java6.Exception.LoginRateLimitExceededException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final Duration FAILED_ATTEMPT_WINDOW = Duration.ofMinutes(1);
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final ConcurrentMap<Integer, LoginAttemptTracker> attempts = new ConcurrentHashMap<>();

    @Override
    public void validateAttemptAllowed(Integer customerId) {
        if (customerId == null) {
            return;
        }

        LoginAttemptTracker tracker = attempts.get(customerId);
        if (tracker == null) {
            return;
        }

        if (tracker.isBlocked()) {
            throw new LoginRateLimitExceededException();
        }
    }

    @Override
    public void recordFailedAttempt(Integer customerId) {
        if (customerId == null) {
            return;
        }

        LoginAttemptTracker tracker = attempts.computeIfAbsent(customerId, ignored -> new LoginAttemptTracker());
        if (tracker.isBlocked()) {
            throw new LoginRateLimitExceededException();
        }

        ConsumptionProbe probe = tracker.failedAttemptBucket.tryConsumeAndReturnRemaining(1);
        if (probe.getRemainingTokens() == 0) {
            tracker.blockedUntil = Instant.now().plus(BLOCK_DURATION);
            throw new LoginRateLimitExceededException();
        }
    }

    @Override
    public void recordSuccessfulAttempt(Integer customerId) {
        if (customerId == null) {
            return;
        }

        attempts.remove(customerId);
    }

    private static final class LoginAttemptTracker {
        private final Bucket failedAttemptBucket = Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(3)
                        .refillGreedy(3, FAILED_ATTEMPT_WINDOW)
                        .build())
                .build();

        private volatile Instant blockedUntil;

        private boolean isBlocked() {
            if (blockedUntil == null) {
                return false;
            }

            if (blockedUntil.isAfter(Instant.now())) {
                return true;
            }

            blockedUntil = null;
            return false;
        }
    }
}

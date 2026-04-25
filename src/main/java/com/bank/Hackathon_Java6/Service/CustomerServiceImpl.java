package com.bank.Hackathon_Java6.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Dto.ForgotCustomerIdRequestDTO;
import com.bank.Hackathon_Java6.Entity.Customer;
import com.bank.Hackathon_Java6.Exception.EmailAlreadyExistsException;
import com.bank.Hackathon_Java6.Exception.InvalidCredentialsException;
import com.bank.Hackathon_Java6.Repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final RegistrationMailService registrationMailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    private final Random random = new Random();

    @Override
    public Map<String, Object> register(CustomerRegisterDTO dto) {
        repository.findByEmail(dto.getEmail())
                .ifPresent(c -> {
                    throw new EmailAlreadyExistsException(dto.getEmail());
                });

        Integer customerId = generateCustomerId();

        Customer customer = Customer.builder()
                .customerId(customerId)
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(customer);
        MailDeliveryResult mailDeliveryResult = registrationMailService.sendRegistrationSuccessEmail(customer);
        String token = jwtService.generateToken(customerId);

        return Map.of(
                "customerId", customerId,
                "name", customer.getName(),
                "tokenType", "Bearer",
                "token", token,
                "message", "Registration successful",
                "mailStatus", mailDeliveryResult.status(),
                "mailMessage", mailDeliveryResult.message()
        );
    }

    @Override
    public Map<String, Object> login(CustomerLoginDTO dto) {
        loginAttemptService.validateAttemptAllowed(dto.getCustomerId());

        Customer customer = repository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    loginAttemptService.recordFailedAttempt(dto.getCustomerId());
                    return new InvalidCredentialsException();
                });

        if (!passwordMatches(dto.getPassword(), customer.getPasswordHash())) {
            loginAttemptService.recordFailedAttempt(dto.getCustomerId());
            throw new InvalidCredentialsException();
        }

        encryptLegacyPlainTextPasswordIfNeeded(dto.getPassword(), customer);
        loginAttemptService.recordSuccessfulAttempt(dto.getCustomerId());
        String token = jwtService.generateToken(customer.getCustomerId());

        return Map.of(
                "customerId", customer.getCustomerId(),
                "name", customer.getName(),
                "tokenType", "Bearer",
                "token", token,
                "message", "Login successful"
        );
    }

    @Override
    public Map<String, Object> forgotCustomerId(ForgotCustomerIdRequestDTO dto) {
        return repository.findByEmail(dto.getEmail())
                .map(customer -> {
                    MailDeliveryResult mailDeliveryResult = registrationMailService.sendCustomerIdReminderEmail(customer);
                    return Map.<String, Object>of(
                            "emailExists", true,
                            "customerId", customer.getCustomerId(),
                            "message", "Customer ID reminder processed",
                            "mailStatus", mailDeliveryResult.status(),
                            "mailMessage", mailDeliveryResult.message()
                    );
                })
                .orElseGet(() -> Map.<String, Object>of(
                        "emailExists", false,
                        "message", "No customer found for this email"
                ));
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (isEncryptedPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword != null && storedPassword.equals(rawPassword);
    }

    private void encryptLegacyPlainTextPasswordIfNeeded(String rawPassword, Customer customer) {
        if (!isEncryptedPassword(customer.getPasswordHash())) {
            customer.setPasswordHash(passwordEncoder.encode(rawPassword));
            repository.save(customer);
        }
    }

    private boolean isEncryptedPassword(String password) {
        return password != null && password.startsWith("$2");
    }

    private Integer generateCustomerId() {
        return 10000 + random.nextInt(90000);
    }
}

package com.bank.Hackathon_Java6.Service;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Entity.Customer;
import com.bank.Hackathon_Java6.Exception.EmailAlreadyExistsException;
import com.bank.Hackathon_Java6.Exception.InvalidCredentialsException;
import com.bank.Hackathon_Java6.Repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	
    private final CustomerRepository repository ;
    private final RegistrationMailService registrationMailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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

        Customer customer = repository.findById(dto.getCustomerId())
                .orElseThrow(InvalidCredentialsException::new); // ✅ don't expose "not found"

        if (!passwordMatches(dto.getPassword(), customer.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtService.generateToken(customer.getCustomerId());

        return Map.of(
                "customerId", customer.getCustomerId(),
                "name", customer.getName(),
                "tokenType", "Bearer",
                "token", token,
                "message", "Login successful"
        );
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword != null && storedPassword.equals(rawPassword);
    }

    private Integer generateCustomerId() {
        return 10000 + random.nextInt(90000); // 5-digit random ID
    }
}

package com.bank.Hackathon_Java6.Service;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

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
                .passwordHash(dto.getPassword()) // plain for now (as per your requirement)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(customer);
        MailDeliveryResult mailDeliveryResult = registrationMailService.sendRegistrationSuccessEmail(customer);

        return Map.of(
                "customerId", customerId,
                "name", customer.getName(),
                "message", "Registration successful",
                "mailStatus", mailDeliveryResult.status(),
                "mailMessage", mailDeliveryResult.message()
        );
    }

    @Override
    public Map<String, Object> login(CustomerLoginDTO dto) {

        Customer customer = repository.findById(dto.getCustomerId())
                .orElseThrow(InvalidCredentialsException::new); // ✅ don't expose "not found"

        // ✅ Password validation
        if (!customer.getPasswordHash().equals(dto.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return Map.of(
                "customerId", customer.getCustomerId(),
                "name", customer.getName(),
                "message", "Login successful"
        );
    }

    private Integer generateCustomerId() {
        return 10000 + random.nextInt(90000); // 5-digit random ID
    }
}

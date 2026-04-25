package com.bank.Hackathon_Java6.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Entity.Customer;
import com.bank.Hackathon_Java6.Exception.EmailAlreadyExistsException;
import com.bank.Hackathon_Java6.Exception.InvalidCredentialsException;
import com.bank.Hackathon_Java6.Repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

class CustomerServiceImplTest {

    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final RegistrationMailService registrationMailService = mock(RegistrationMailService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final CustomerServiceImpl service = new CustomerServiceImpl(
            customerRepository,
            registrationMailService,
            jwtService,
            passwordEncoder
    );

    @Test
    void registerEncryptsPasswordSavesCustomerSendsMailAndReturnsToken() {
        CustomerRegisterDTO dto = new CustomerRegisterDTO();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        dto.setPassword("secret");

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(registrationMailService.sendRegistrationSuccessEmail(any(Customer.class)))
                .thenReturn(new MailDeliveryResult("SKIPPED", "Mail disabled"));
        when(jwtService.generateToken(any(Integer.class))).thenReturn("jwt-token");

        Map<String, Object> response = service.register(dto);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        assertThat(savedCustomer.getPasswordHash()).isNotEqualTo("secret");
        assertThat(passwordEncoder.matches("secret", savedCustomer.getPasswordHash())).isTrue();
        assertThat(response)
                .containsEntry("name", "Test User")
                .containsEntry("tokenType", "Bearer")
                .containsEntry("token", "jwt-token")
                .containsEntry("mailStatus", "SKIPPED");
    }

    @Test
    void registerThrowsWhenEmailAlreadyExists() {
        CustomerRegisterDTO dto = new CustomerRegisterDTO();
        dto.setEmail("taken@example.com");

        when(customerRepository.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(Customer.builder().email("taken@example.com").build()));

        assertThatThrownBy(() -> service.register(dto))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void loginAcceptsEncryptedPasswordAndReturnsToken() {
        Customer customer = Customer.builder()
                .customerId(12345)
                .name("Test User")
                .passwordHash(passwordEncoder.encode("secret"))
                .createdAt(LocalDateTime.now())
                .build();

        CustomerLoginDTO dto = CustomerLoginDTO.builder()
                .customerId(12345)
                .password("secret")
                .build();

        when(customerRepository.findById(12345)).thenReturn(Optional.of(customer));
        when(jwtService.generateToken(12345)).thenReturn("jwt-token");

        Map<String, Object> response = service.login(dto);

        assertThat(response)
                .containsEntry("customerId", 12345)
                .containsEntry("tokenType", "Bearer")
                .containsEntry("token", "jwt-token");
    }

    @Test
    void loginMigratesLegacyPlainTextPasswordToEncryptedPassword() {
        Customer customer = Customer.builder()
                .customerId(12345)
                .name("Legacy User")
                .passwordHash("legacy-password")
                .createdAt(LocalDateTime.now())
                .build();

        CustomerLoginDTO dto = CustomerLoginDTO.builder()
                .customerId(12345)
                .password("legacy-password")
                .build();

        when(customerRepository.findById(12345)).thenReturn(Optional.of(customer));
        when(jwtService.generateToken(12345)).thenReturn("jwt-token");

        service.login(dto);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertThat(passwordEncoder.matches("legacy-password", customerCaptor.getValue().getPasswordHash())).isTrue();
    }

    @Test
    void loginThrowsForMissingCustomerOrWrongPassword() {
        CustomerLoginDTO missingCustomer = CustomerLoginDTO.builder()
                .customerId(99999)
                .password("secret")
                .build();

        when(customerRepository.findById(99999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(missingCustomer))
                .isInstanceOf(InvalidCredentialsException.class);

        CustomerLoginDTO wrongPassword = CustomerLoginDTO.builder()
                .customerId(12345)
                .password("wrong")
                .build();
        Customer customer = Customer.builder()
                .customerId(12345)
                .passwordHash(passwordEncoder.encode("secret"))
                .build();

        when(customerRepository.findById(12345)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> service.login(wrongPassword))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}

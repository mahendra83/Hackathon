package com.bank.Hackathon_Java6.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Service.CustomerService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class CustomerControllerTest {

    private final CustomerService customerService = mock(CustomerService.class);
    private final CustomerController controller = new CustomerController(customerService);

    @Test
    void registerDelegatesToService() {
        CustomerRegisterDTO dto = new CustomerRegisterDTO();
        when(customerService.register(dto)).thenReturn(Map.of("message", "Registration successful"));

        ResponseEntity<Map<String, Object>> response = controller.register(dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsEntry("message", "Registration successful");
        verify(customerService).register(dto);
    }

    @Test
    void loginDelegatesToService() {
        CustomerLoginDTO dto = CustomerLoginDTO.builder()
                .customerId(12345)
                .password("secret")
                .build();
        when(customerService.login(dto)).thenReturn(Map.of("message", "Login successful"));

        ResponseEntity<Map<String, Object>> response = controller.login(dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsEntry("message", "Login successful");
        verify(customerService).login(dto);
    }
}

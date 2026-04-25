package com.bank.Hackathon_Java6.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Dto.ForgotCustomerIdRequestDTO;
import com.bank.Hackathon_Java6.Dto.ResetPasswordRequestDTO;
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

    @Test
    void forgotCustomerIdDelegatesToService() {
        ForgotCustomerIdRequestDTO dto = ForgotCustomerIdRequestDTO.builder()
                .email("test@example.com")
                .build();
        when(customerService.forgotCustomerId(dto)).thenReturn(Map.of(
                "emailExists", true,
                "message", "Customer ID reminder processed"
        ));

        ResponseEntity<Map<String, Object>> response = controller.forgotCustomerId(dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody())
                .containsEntry("emailExists", true)
                .containsEntry("message", "Customer ID reminder processed");
        verify(customerService).forgotCustomerId(dto);
    }

    @Test
    void resetPasswordDelegatesToService() {
        ResetPasswordRequestDTO dto = ResetPasswordRequestDTO.builder()
                .customerId(12345)
                .newPassword("newSecret")
                .build();
        when(customerService.resetPassword(dto)).thenReturn(Map.of(
                "customerId", 12345,
                "message", "Password reset successful"
        ));

        ResponseEntity<Map<String, Object>> response = controller.resetPassword(dto);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody())
                .containsEntry("customerId", 12345)
                .containsEntry("message", "Password reset successful");
        verify(customerService).resetPassword(dto);
    }
}

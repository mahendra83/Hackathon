package com.bank.Hackathon_Java6.Controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Dto.FavoriteAccountRequestDTO;
import com.bank.Hackathon_Java6.Dto.FavoriteAccountResponseDTO;
import com.bank.Hackathon_Java6.Dto.PagedResponseDTO;
import com.bank.Hackathon_Java6.Service.FavoriteAccountService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class FavoriteAccountControllerTest {

    private final FavoriteAccountService favoriteAccountService = mock(FavoriteAccountService.class);
    private final FavoriteAccountController controller = new FavoriteAccountController(favoriteAccountService);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllAccountsDelegatesWhenAuthenticatedCustomerMatchesPath() {
        authenticateAs(12345);
        PagedResponseDTO<FavoriteAccountResponseDTO> pagedResponse = PagedResponseDTO.<FavoriteAccountResponseDTO>builder()
                .content(List.of(response(10)))
                .pageNumber(0)
                .pageSize(5)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(favoriteAccountService.getAccountsByCustomer(12345, 0, 5)).thenReturn(pagedResponse);

        ResponseEntity<PagedResponseDTO<FavoriteAccountResponseDTO>> response = controller.getAllAccounts(12345, 0, 5);

        assertThat(response.getBody().getContent()).hasSize(1);
        verify(favoriteAccountService).getAccountsByCustomer(12345, 0, 5);
    }

    @Test
    void getAccountByIdDelegatesWhenAuthenticatedCustomerMatchesPath() {
        authenticateAs(12345);
        when(favoriteAccountService.getAccountById(12345, 10)).thenReturn(response(10));

        ResponseEntity<FavoriteAccountResponseDTO> response = controller.getAccountById(12345, 10);

        assertThat(response.getBody().getAccountId()).isEqualTo(10);
        verify(favoriteAccountService).getAccountById(12345, 10);
    }

    @Test
    void createAccountReturnsCreatedWhenAuthenticatedCustomerMatchesPath() {
        authenticateAs(12345);
        FavoriteAccountRequestDTO request = FavoriteAccountRequestDTO.builder()
                .accountName("Payee")
                .iban("ES2100010000000000")
                .bankCode(1)
                .build();
        when(favoriteAccountService.createAccount(12345, request)).thenReturn(response(10));

        ResponseEntity<FavoriteAccountResponseDTO> response = controller.createAccount(12345, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getAccountId()).isEqualTo(10);
        verify(favoriteAccountService).createAccount(12345, request);
    }

    @Test
    void updateAccountDelegatesWhenAuthenticatedCustomerMatchesPath() {
        authenticateAs(12345);
        FavoriteAccountRequestDTO request = FavoriteAccountRequestDTO.builder()
                .accountName("Updated")
                .iban("ES2100010000000000")
                .bankCode(1)
                .build();
        when(favoriteAccountService.updateAccount(12345, 10, request)).thenReturn(response(10));

        ResponseEntity<FavoriteAccountResponseDTO> response = controller.updateAccount(12345, 10, request);

        assertThat(response.getBody().getAccountId()).isEqualTo(10);
        verify(favoriteAccountService).updateAccount(12345, 10, request);
    }

    @Test
    void deleteAccountReturnsNoContentWhenAuthenticatedCustomerMatchesPath() {
        authenticateAs(12345);

        ResponseEntity<Void> response = controller.deleteAccount(12345, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(favoriteAccountService).deleteAccount(12345, 10);
    }

    @Test
    void protectedMethodsRejectDifferentAuthenticatedCustomer() {
        authenticateAs(54321);

        assertThatThrownBy(() -> controller.getAllAccounts(12345, 0, 5))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> controller.getAccountById(12345, 10))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> controller.createAccount(12345, FavoriteAccountRequestDTO.builder().build()))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> controller.updateAccount(12345, 10, FavoriteAccountRequestDTO.builder().build()))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> controller.deleteAccount(12345, 10))
                .isInstanceOf(AccessDeniedException.class);
    }

    private void authenticateAs(Integer customerId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customerId, null, List.of())
        );
    }

    private FavoriteAccountResponseDTO response(Integer accountId) {
        return FavoriteAccountResponseDTO.builder()
                .accountId(accountId)
                .customerId(12345)
                .accountName("Payee")
                .iban("ES2100010000000000")
                .bankCode(1)
                .bankName("Bank")
                .build();
    }
}

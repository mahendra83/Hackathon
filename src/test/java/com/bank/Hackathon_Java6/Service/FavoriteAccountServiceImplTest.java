package com.bank.Hackathon_Java6.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Dto.FavoriteAccountRequestDTO;
import com.bank.Hackathon_Java6.Dto.FavoriteAccountResponseDTO;
import com.bank.Hackathon_Java6.Dto.PagedResponseDTO;
import com.bank.Hackathon_Java6.Entity.Bank;
import com.bank.Hackathon_Java6.Entity.Customer;
import com.bank.Hackathon_Java6.Entity.FavoriteAccount;
import com.bank.Hackathon_Java6.Exception.BankNotFoundException;
import com.bank.Hackathon_Java6.Exception.CustomerNotFoundException;
import com.bank.Hackathon_Java6.Exception.FavoriteAccountNotFoundException;
import com.bank.Hackathon_Java6.Exception.MaxFavoriteAccountsExceededException;
import com.bank.Hackathon_Java6.Repository.BankRepository;
import com.bank.Hackathon_Java6.Repository.CustomerRepository;
import com.bank.Hackathon_Java6.Repository.FavoriteAccountRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.mock;

class FavoriteAccountServiceImplTest {

    private final FavoriteAccountRepository favoriteAccountRepository = mock(FavoriteAccountRepository.class);
    private final CustomerRepository customerRepository = mock(CustomerRepository.class);
    private final BankRepository bankRepository = mock(BankRepository.class);
    private final IbanBankResolverService ibanBankResolverService = mock(IbanBankResolverService.class);
    private final FavoriteAccountServiceImpl service = new FavoriteAccountServiceImpl(
            favoriteAccountRepository,
            customerRepository,
            bankRepository,
            ibanBankResolverService
    );

    @Test
    void getAccountsByCustomerReturnsPagedResponseAndUsesDefaultSizeWhenInvalid() {
        FavoriteAccount account = favoriteAccount(1, "Payee", "ES2100010000000000");

        when(customerRepository.existsById(12345)).thenReturn(true);
        when(favoriteAccountRepository.findByCustomer_CustomerId(12345, PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(account), PageRequest.of(0, 5), 1));

        PagedResponseDTO<FavoriteAccountResponseDTO> response = service.getAccountsByCustomer(12345, 0, 0);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getPageSize()).isEqualTo(5);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getAccountName()).isEqualTo("Payee");
    }

    @Test
    void getAccountsByCustomerThrowsWhenCustomerMissing() {
        when(customerRepository.existsById(12345)).thenReturn(false);

        assertThatThrownBy(() -> service.getAccountsByCustomer(12345, 0, 5))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void getAccountByIdReturnsAccount() {
        FavoriteAccount account = favoriteAccount(10, "Payee", "ES2100010000000000");

        when(customerRepository.existsById(12345)).thenReturn(true);
        when(favoriteAccountRepository.findByAccountIdAndCustomer_CustomerId(10, 12345))
                .thenReturn(Optional.of(account));

        FavoriteAccountResponseDTO response = service.getAccountById(12345, 10);

        assertThat(response.getAccountId()).isEqualTo(10);
        assertThat(response.getBankCode()).isEqualTo(1);
    }

    @Test
    void getAccountByIdThrowsWhenAccountMissing() {
        when(customerRepository.existsById(12345)).thenReturn(true);
        when(favoriteAccountRepository.findByAccountIdAndCustomer_CustomerId(10, 12345))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAccountById(12345, 10))
                .isInstanceOf(FavoriteAccountNotFoundException.class);
    }

    @Test
    void createAccountUsesBankCodeFromIbanAndSaves() {
        Customer customer = customer(12345);
        Bank bank = bank(1);
        FavoriteAccount saved = favoriteAccount(20, "New Payee", "ES2100010000000000");
        FavoriteAccountRequestDTO request = request("New Payee", "ES2100010000000000", 999);

        when(customerRepository.findById(12345)).thenReturn(Optional.of(customer));
        when(favoriteAccountRepository.countByCustomer_CustomerId(12345)).thenReturn(0L);
        when(ibanBankResolverService.extractBankCodeFromIban("ES2100010000000000")).thenReturn(1);
        when(bankRepository.findById(1)).thenReturn(Optional.of(bank));
        when(favoriteAccountRepository.save(any(FavoriteAccount.class))).thenReturn(saved);

        FavoriteAccountResponseDTO response = service.createAccount(12345, request);

        ArgumentCaptor<FavoriteAccount> accountCaptor = ArgumentCaptor.forClass(FavoriteAccount.class);
        verify(favoriteAccountRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getBank().getBankCode()).isEqualTo(1);
        assertThat(response.getAccountId()).isEqualTo(20);
    }

    @Test
    void createAccountThrowsForMissingCustomerMaxLimitOrMissingBank() {
        FavoriteAccountRequestDTO request = request("Payee", "ES2100010000000000", 1);

        when(customerRepository.findById(12345)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createAccount(12345, request))
                .isInstanceOf(CustomerNotFoundException.class);

        when(customerRepository.findById(12345)).thenReturn(Optional.of(customer(12345)));
        when(favoriteAccountRepository.countByCustomer_CustomerId(12345)).thenReturn(20L);
        assertThatThrownBy(() -> service.createAccount(12345, request))
                .isInstanceOf(MaxFavoriteAccountsExceededException.class);

        when(favoriteAccountRepository.countByCustomer_CustomerId(12345)).thenReturn(0L);
        when(ibanBankResolverService.extractBankCodeFromIban("ES2100010000000000")).thenReturn(null);
        when(bankRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createAccount(12345, request))
                .isInstanceOf(BankNotFoundException.class);
    }

    @Test
    void updateAccountUpdatesFieldsAndSaves() {
        FavoriteAccount existing = favoriteAccount(10, "Old", "ES2100010000000000");
        Bank newBank = bank(2);
        FavoriteAccountRequestDTO request = request("Updated", "ES2100020000000000", 2);

        when(customerRepository.existsById(12345)).thenReturn(true);
        when(favoriteAccountRepository.findByAccountIdAndCustomer_CustomerId(10, 12345))
                .thenReturn(Optional.of(existing));
        when(ibanBankResolverService.extractBankCodeFromIban("ES2100020000000000")).thenReturn(2);
        when(bankRepository.findById(2)).thenReturn(Optional.of(newBank));
        when(favoriteAccountRepository.save(existing)).thenReturn(existing);

        FavoriteAccountResponseDTO response = service.updateAccount(12345, 10, request);

        assertThat(response.getAccountName()).isEqualTo("Updated");
        assertThat(response.getIban()).isEqualTo("ES2100020000000000");
        assertThat(response.getBankCode()).isEqualTo(2);
    }

    @Test
    void deleteAccountDeletesExistingAccount() {
        FavoriteAccount account = favoriteAccount(10, "Payee", "ES2100010000000000");

        when(customerRepository.existsById(12345)).thenReturn(true);
        when(favoriteAccountRepository.findByAccountIdAndCustomer_CustomerId(10, 12345))
                .thenReturn(Optional.of(account));

        service.deleteAccount(12345, 10);

        verify(favoriteAccountRepository).delete(account);
    }

    private FavoriteAccountRequestDTO request(String accountName, String iban, Integer bankCode) {
        return FavoriteAccountRequestDTO.builder()
                .accountName(accountName)
                .iban(iban)
                .bankCode(bankCode)
                .build();
    }

    private FavoriteAccount favoriteAccount(Integer accountId, String accountName, String iban) {
        FavoriteAccount account = FavoriteAccount.builder()
                .customer(customer(12345))
                .accountName(accountName)
                .iban(iban)
                .bank(bank(1))
                .build();
        account.setAccountId(accountId);
        return account;
    }

    private Customer customer(Integer customerId) {
        return Customer.builder()
                .customerId(customerId)
                .name("Customer")
                .email("customer@example.com")
                .passwordHash("hash")
                .build();
    }

    private Bank bank(Integer bankCode) {
        return Bank.builder()
                .bankCode(bankCode)
                .bankName("Bank " + bankCode)
                .build();
    }
}

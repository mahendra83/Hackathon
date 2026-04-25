package com.bank.Hackathon_Java6.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteAccountServiceImpl implements FavoriteAccountService {

    private static final int MAX_FAVORITE_ACCOUNTS = 20;
    private static final int DEFAULT_PAGE_SIZE = 5;

    private final FavoriteAccountRepository favoriteAccountRepository;
    private final CustomerRepository customerRepository;
    private final BankRepository bankRepository;
    private final IbanBankResolverService ibanBankResolverService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<FavoriteAccountResponseDTO> getAccountsByCustomer(Integer customerId, int page, int size) {
        validateCustomerExists(customerId);

        int pageSize = (size <= 0) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<FavoriteAccount> accountPage = favoriteAccountRepository
                .findByCustomer_CustomerId(customerId, pageable);

        List<FavoriteAccountResponseDTO> content = accountPage.getContent()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<FavoriteAccountResponseDTO>builder()
                .content(content)
                .pageNumber(accountPage.getNumber())
                .pageSize(accountPage.getSize())
                .totalElements(accountPage.getTotalElements())
                .totalPages(accountPage.getTotalPages())
                .last(accountPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteAccountResponseDTO getAccountById(Integer customerId, Integer accountId) {
        validateCustomerExists(customerId);
        FavoriteAccount account = favoriteAccountRepository
                .findByAccountIdAndCustomer_CustomerId(accountId, customerId)
                .orElseThrow(() -> new FavoriteAccountNotFoundException(accountId, customerId));
        return toResponseDTO(account);
    }

    @Override
    public FavoriteAccountResponseDTO createAccount(Integer customerId, FavoriteAccountRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        long currentCount = favoriteAccountRepository.countByCustomer_CustomerId(customerId);
        if (currentCount >= MAX_FAVORITE_ACCOUNTS) {
            throw new MaxFavoriteAccountsExceededException();
        }

        // Resolve bank: use bankCode from request, but validate via IBAN auto-calculation
        Integer bankCodeFromIban = ibanBankResolverService.extractBankCodeFromIban(requestDTO.getIban());
        Integer bankCode = (bankCodeFromIban != null) ? bankCodeFromIban : requestDTO.getBankCode();

        Bank bank = bankRepository.findById(bankCode)
                .orElseThrow(() -> new BankNotFoundException(bankCode));

        FavoriteAccount account = FavoriteAccount.builder()
                .customer(customer)
                .accountName(requestDTO.getAccountName())
                .iban(requestDTO.getIban())
                .bank(bank)
                .build();

        FavoriteAccount saved = favoriteAccountRepository.save(account);
        return toResponseDTO(saved);
    }

    @Override
    public FavoriteAccountResponseDTO updateAccount(Integer customerId, Integer accountId,
                                                     FavoriteAccountRequestDTO requestDTO) {
        validateCustomerExists(customerId);

        FavoriteAccount account = favoriteAccountRepository
                .findByAccountIdAndCustomer_CustomerId(accountId, customerId)
                .orElseThrow(() -> new FavoriteAccountNotFoundException(accountId, customerId));

        Integer bankCodeFromIban = ibanBankResolverService.extractBankCodeFromIban(requestDTO.getIban());
        Integer bankCode = (bankCodeFromIban != null) ? bankCodeFromIban : requestDTO.getBankCode();

        Bank bank = bankRepository.findById(bankCode)
                .orElseThrow(() -> new BankNotFoundException(bankCode));

        account.setAccountName(requestDTO.getAccountName());
        account.setIban(requestDTO.getIban());
        account.setBank(bank);

        FavoriteAccount updated = favoriteAccountRepository.save(account);
        return toResponseDTO(updated);
    }

    @Override
    public void deleteAccount(Integer customerId, Integer accountId) {
        validateCustomerExists(customerId);
        FavoriteAccount account = favoriteAccountRepository
                .findByAccountIdAndCustomer_CustomerId(accountId, customerId)
                .orElseThrow(() -> new FavoriteAccountNotFoundException(accountId, customerId));
        favoriteAccountRepository.delete(account);
    }

    private void validateCustomerExists(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
    }

    private FavoriteAccountResponseDTO toResponseDTO(FavoriteAccount account) {
        return FavoriteAccountResponseDTO.builder()
                .accountId(account.getAccountId())
                .customerId(account.getCustomer().getCustomerId())
                .accountName(account.getAccountName())
                .iban(account.getIban())
                .bankCode(account.getBank().getBankCode())
                .bankName(account.getBank().getBankName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

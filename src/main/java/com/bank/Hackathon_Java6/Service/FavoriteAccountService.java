package com.bank.Hackathon_Java6.Service;

import com.bank.Hackathon_Java6.Dto.FavoriteAccountRequestDTO;
import com.bank.Hackathon_Java6.Dto.FavoriteAccountResponseDTO;
import com.bank.Hackathon_Java6.Dto.PagedResponseDTO;

public interface FavoriteAccountService {
    PagedResponseDTO<FavoriteAccountResponseDTO> getAccountsByCustomer(Integer customerId, int page, int size);
    FavoriteAccountResponseDTO getAccountById(Integer customerId, Integer accountId);
    FavoriteAccountResponseDTO createAccount(Integer customerId, FavoriteAccountRequestDTO requestDTO);
    FavoriteAccountResponseDTO updateAccount(Integer customerId, Integer accountId, FavoriteAccountRequestDTO requestDTO);
    void deleteAccount(Integer customerId, Integer accountId);
}

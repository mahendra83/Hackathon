package com.bank.Hackathon_Java6.Dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteAccountResponseDTO {

    private Integer accountId;
    private Integer customerId;
    private String accountName;
    private String iban;
    private Integer bankCode;
    private String bankName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


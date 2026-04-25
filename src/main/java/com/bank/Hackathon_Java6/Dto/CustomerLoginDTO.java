package com.bank.Hackathon_Java6.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLoginDTO {

	private Integer customerId;
    private String password;
}
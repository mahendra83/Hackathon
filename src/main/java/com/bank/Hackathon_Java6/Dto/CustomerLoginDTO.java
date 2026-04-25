package com.bank.Hackathon_Java6.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerLoginDTO {

    @NotNull(message = "Customer ID is mandatory")
	private Integer customerId;
    @NotBlank(message = "Password is mandatory")
    private String password;
}

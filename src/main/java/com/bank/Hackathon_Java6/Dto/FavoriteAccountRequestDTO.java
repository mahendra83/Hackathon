package com.bank.Hackathon_Java6.Dto;



import com.bank.Hackathon_Java6.validation.ValidIban;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteAccountRequestDTO {

    @NotBlank(message = "Account name is mandatory")
    @Pattern(
        regexp = "^[a-zA-Z0-9 '\\-]+$",
        message = "Name must contain only letters, numbers, spaces, apostrophes, or hyphens"
    )
    private String accountName;

    @NotBlank(message = "IBAN is mandatory")
    @Size(max = 20, message = "IBAN must not exceed 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "IBAN must contain only letters and numbers")
   @ValidIban
    private String iban;

    @NotNull(message = "Bank code is mandatory")
    private Integer bankCode;
}

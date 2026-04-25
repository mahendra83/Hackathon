package com.bank.Hackathon_Java6.Dto;


import jakarta.validation.constraints.*;

public class FavoriteAccountRequest {

    @NotNull(message = "Customer ID is mandatory")
    private Integer customerId;

    @NotBlank(message = "Account name is mandatory")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 '\\-]+$",
             message = "Account name must contain only letters, numbers, spaces, apostrophes, or hyphens")
    private String accountName;

    @NotBlank(message = "IBAN is mandatory")
    @Size(max = 20, message = "IBAN must not exceed 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "IBAN must contain only letters and numbers")
    private String iban;

    // bankCode is auto-derived from IBAN (characters at positions 4–7)
    // Bank field is disabled on the UI (auto-calculated)

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}


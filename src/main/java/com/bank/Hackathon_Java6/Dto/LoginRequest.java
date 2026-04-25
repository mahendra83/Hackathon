package com.bank.Hackathon_Java6.Dto;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {

    @NotNull(message = "Customer ID is mandatory")
    private Integer customerId;

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
}

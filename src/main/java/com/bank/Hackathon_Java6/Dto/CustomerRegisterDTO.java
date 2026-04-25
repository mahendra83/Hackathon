package com.bank.Hackathon_Java6.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRegisterDTO {

	private String name;
    private String email;
    private String password;
}

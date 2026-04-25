package com.bank.Hackathon_Java6.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRegisterDTO {

	@NotBlank(message = "Name is mandatory")
	private String name;
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;
}

package com.bank.Hackathon_Java6.Service;


import java.util.Map;

import com.bank.Hackathon_Java6.Dto.CustomerLoginDTO;
import com.bank.Hackathon_Java6.Dto.CustomerRegisterDTO;
import com.bank.Hackathon_Java6.Dto.ForgotCustomerIdRequestDTO;
import com.bank.Hackathon_Java6.Dto.ResetPasswordRequestDTO;

public interface CustomerService {

    Map<String, Object> register(CustomerRegisterDTO dto);

    Map<String, Object> login(CustomerLoginDTO dto);

    Map<String, Object> forgotCustomerId(ForgotCustomerIdRequestDTO dto);

    Map<String, Object> resetPassword(ResetPasswordRequestDTO dto);
}

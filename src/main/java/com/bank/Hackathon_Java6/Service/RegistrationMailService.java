package com.bank.Hackathon_Java6.Service;

import com.bank.Hackathon_Java6.Entity.Customer;

public interface RegistrationMailService {

    MailDeliveryResult sendRegistrationSuccessEmail(Customer customer);

    MailDeliveryResult sendCustomerIdReminderEmail(Customer customer);
}

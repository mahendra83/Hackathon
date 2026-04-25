package com.bank.Hackathon_Java6.Service;

import com.bank.Hackathon_Java6.Entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationMailServiceImpl implements RegistrationMailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String fromAddress;

    @Override
    public MailDeliveryResult sendRegistrationSuccessEmail(Customer customer) {
        return sendMail(
                customer,
                "Registration successful",
                buildRegistrationMessage(customer),
                "Registration email"
        );
    }

    @Override
    public MailDeliveryResult sendCustomerIdReminderEmail(Customer customer) {
        return sendMail(
                customer,
                "Customer ID reminder",
                buildCustomerIdReminderMessage(customer),
                "Customer ID reminder email"
        );
    }

    private MailDeliveryResult sendMail(Customer customer, String subject, String body, String logPrefix) {
        if (!mailEnabled) {
            log.info("{} skipped for customer {} because app.mail.enabled=false", logPrefix, customer.getCustomerId());
            return new MailDeliveryResult("SKIPPED", "Mail disabled because app.mail.enabled=false");
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("{} skipped for customer {} because app.mail.from is not configured", logPrefix, customer.getCustomerId());
            return new MailDeliveryResult("SKIPPED", "Mail sender is not configured");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(customer.getEmail());
            helper.setSubject(subject);
            helper.setText(body, false);
            mailSender.send(message);
            log.info("{} sent to {}", logPrefix, customer.getEmail());
            return new MailDeliveryResult("SENT", subject + " sent successfully");
        } catch (MailAuthenticationException ex) {
            log.error("Mail authentication failed for sender {}", fromAddress, ex);
            return new MailDeliveryResult("FAILED", "SMTP authentication failed. For Gmail, use an App Password instead of your normal password.");
        } catch (MailException | MessagingException ex) {
            log.error("Failed to send {} to {}", logPrefix, customer.getEmail(), ex);
            return new MailDeliveryResult("FAILED", subject + " could not be sent");
        }
    }

    private String buildRegistrationMessage(Customer customer) {
        return """
                Hello %s,

                Your registration was completed successfully.

                Customer ID: %s

                You can now use the application with your registered email address.

                Regards,
                Favorite Payee Team
                """.formatted(customer.getName(), customer.getCustomerId());
    }

    private String buildCustomerIdReminderMessage(Customer customer) {
        return """
                Hello %s,

                We received a request to remind you of your customer ID.

                Your customer ID is: %s

                Regards,
                Favorite Payee Team
                """.formatted(customer.getName(), customer.getCustomerId());
    }
}

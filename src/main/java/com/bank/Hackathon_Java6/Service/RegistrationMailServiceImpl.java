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
        if (!mailEnabled) {
            log.info("Registration email skipped for customer {} because app.mail.enabled=false", customer.getCustomerId());
            return new MailDeliveryResult("SKIPPED", "Mail disabled because app.mail.enabled=false");
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Registration email skipped for customer {} because app.mail.from is not configured", customer.getCustomerId());
            return new MailDeliveryResult("SKIPPED", "Mail sender is not configured");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(customer.getEmail());
            helper.setSubject("Registration successful");
            helper.setText(buildMessage(customer), false);
            mailSender.send(message);
            log.info("Registration email sent to {}", customer.getEmail());
            return new MailDeliveryResult("SENT", "Registration email sent successfully");
        } catch (MailAuthenticationException ex) {
            log.error("Mail authentication failed for sender {}", fromAddress, ex);
            return new MailDeliveryResult("FAILED", "SMTP authentication failed. For Gmail, use an App Password instead of your normal password.");
        } catch (MailException | MessagingException ex) {
            log.error("Failed to send registration email to {}", customer.getEmail(), ex);
            return new MailDeliveryResult("FAILED", "Registration email could not be sent");
        }
    }

    private String buildMessage(Customer customer) {
        return """
                Hello %s,

                Your registration was completed successfully.

                Customer ID: %s

                You can now use the application with your registered email address.

                Regards,
                Favorite Payee Team
                """.formatted(customer.getName(), customer.getCustomerId());
    }
}

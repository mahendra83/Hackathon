package com.bank.Hackathon_Java6.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bank.Hackathon_Java6.Entity.Customer;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

class RegistrationMailServiceImplTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final RegistrationMailServiceImpl service = new RegistrationMailServiceImpl(mailSender);

    @Test
    void sendRegistrationSuccessEmailReturnsSkippedWhenMailDisabled() {
        ReflectionTestUtils.setField(service, "mailEnabled", false);

        MailDeliveryResult result = service.sendRegistrationSuccessEmail(customer());

        assertThat(result.status()).isEqualTo("SKIPPED");
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendRegistrationSuccessEmailReturnsSkippedWhenSenderMissing() {
        ReflectionTestUtils.setField(service, "mailEnabled", true);
        ReflectionTestUtils.setField(service, "fromAddress", "");

        MailDeliveryResult result = service.sendRegistrationSuccessEmail(customer());

        assertThat(result.status()).isEqualTo("SKIPPED");
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendRegistrationSuccessEmailSendsMessageWhenConfigured() {
        ReflectionTestUtils.setField(service, "mailEnabled", true);
        ReflectionTestUtils.setField(service, "fromAddress", "from@example.com");
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));

        when(mailSender.createMimeMessage()).thenReturn(message);

        MailDeliveryResult result = service.sendRegistrationSuccessEmail(customer());

        assertThat(result.status()).isEqualTo("SENT");
        verify(mailSender).send(message);
    }

    @Test
    void sendRegistrationSuccessEmailReturnsFailedForAuthenticationError() {
        ReflectionTestUtils.setField(service, "mailEnabled", true);
        ReflectionTestUtils.setField(service, "fromAddress", "from@example.com");
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));

        when(mailSender.createMimeMessage()).thenReturn(message);
        org.mockito.Mockito.doThrow(new MailAuthenticationException("bad credentials"))
                .when(mailSender).send(message);

        MailDeliveryResult result = service.sendRegistrationSuccessEmail(customer());

        assertThat(result.status()).isEqualTo("FAILED");
        assertThat(result.message()).contains("SMTP authentication failed");
    }

    private Customer customer() {
        return Customer.builder()
                .customerId(12345)
                .name("Test User")
                .email("test@example.com")
                .build();
    }
}

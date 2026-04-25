package com.bank.Hackathon_Java6.Service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "TXlTZWNyZXRLZXlGb3JIYWNrbGF0aG9uSmF2YTYyMDI2TXVzdEJlTG9uZ0Vub3VnaA==";

    @Test
    void generateTokenCreatesTokenWithCustomerIdSubject() {
        JwtService service = new JwtService(SECRET, 60_000);

        String token = service.generateToken(12345);

        assertThat(token).isNotBlank();
        assertThat(service.extractCustomerId(token)).isEqualTo(12345);
        assertThat(service.isTokenValid(token, 12345)).isTrue();
        assertThat(service.isTokenValid(token, 54321)).isFalse();
    }

    @Test
    void invalidOrExpiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1);

        String expiredToken = service.generateToken(12345);

        assertThat(service.extractCustomerId("not-a-token")).isNull();
        assertThat(service.isTokenValid("not-a-token", 12345)).isFalse();
        assertThat(service.isTokenValid(expiredToken, 12345)).isFalse();
    }
}

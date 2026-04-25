package com.bank.Hackathon_Java6.Service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IbanBankResolverServiceImplTest {

    private final IbanBankResolverServiceImpl service = new IbanBankResolverServiceImpl();

    @Test
    void extractBankCodeFromIbanReturnsNumericBankCode() {
        assertThat(service.extractBankCodeFromIban("ES2100010000000000")).isEqualTo(1);
        assertThat(service.extractBankCodeFromIban("ES21 1234 0000")).isEqualTo(1234);
    }

    @Test
    void extractBankCodeFromIbanReturnsNullForInvalidInput() {
        assertThat(service.extractBankCodeFromIban(null)).isNull();
        assertThat(service.extractBankCodeFromIban("ES21")).isNull();
        assertThat(service.extractBankCodeFromIban("ES21ABCD0000")).isNull();
    }
}

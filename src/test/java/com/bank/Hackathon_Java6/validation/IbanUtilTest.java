package com.bank.Hackathon_Java6.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class IbanUtilTest {

    private final IbanUtil util = new IbanUtil();

    @Test
    void extractBankCodeReturnsBankCode() {
        assertThat(util.extractBankCode("ES2100010000000000")).isEqualTo(1);
        assertThat(util.extractBankCode("ES2112340000000000")).isEqualTo(1234);
    }

    @Test
    void extractBankCodeThrowsForShortOrNonNumericIban() {
        assertThatThrownBy(() -> util.extractBankCode("ES21"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> util.extractBankCode("ES21ABCD0000"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isValidFormatChecksAlphanumericLength() {
        assertThat(util.isValidFormat("ES210001")).isTrue();
        assertThat(util.isValidFormat("ES21 0001")).isFalse();
        assertThat(util.isValidFormat("ES21!001")).isFalse();
        assertThat(util.isValidFormat("SHORT")).isFalse();
    }
}

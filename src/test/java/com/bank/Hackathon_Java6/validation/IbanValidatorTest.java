package com.bank.Hackathon_Java6.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IbanValidatorTest {

    private final IbanValidator validator = new IbanValidator();

    @Test
    void isValidAcceptsNullBlankAndValidIbanShape() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid(" ", null)).isTrue();
        assertThat(validator.isValid("ES2100010000000000", null)).isTrue();
        assertThat(validator.isValid("es21 0001 0000", null)).isTrue();
    }

    @Test
    void isValidRejectsInvalidIbanShape() {
        assertThat(validator.isValid("E1210001", null)).isFalse();
        assertThat(validator.isValid("ESAA0001", null)).isFalse();
        assertThat(validator.isValid("ES21", null)).isFalse();
    }
}

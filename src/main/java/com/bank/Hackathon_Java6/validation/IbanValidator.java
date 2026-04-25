package com.bank.Hackathon_Java6.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates IBAN format.
 * Per spec: bank code is extracted from positions 5-8 (0-indexed: chars 4-7).
 * Format example: ES21 0000 0000 0000 0000 0000
 * The bank code (4 digits) starts at position 4 in the raw IBAN string.
 */
public class IbanValidator implements ConstraintValidator<ValidIban, String> {

    private static final int MIN_IBAN_LENGTH = 8;

    @Override
    public boolean isValid(String iban, ConstraintValidatorContext context) {
        if (iban == null || iban.isBlank()) {
            return true; // @NotBlank handles this
        }
        String stripped = iban.replaceAll("\\s", "").toUpperCase();
        if (stripped.length() < MIN_IBAN_LENGTH) {
            return false;
        }
        // Ensure first 2 chars are letters (country code) and next 2 are digits (check digits)
        if (!stripped.substring(0, 2).matches("[A-Z]{2}")) {
            return false;
        }
        if (!stripped.substring(2, 4).matches("[0-9]{2}")) {
            return false;
        }
        return true;
    }
}

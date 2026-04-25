package com.bank.Hackathon_Java6.validation;


import org.springframework.stereotype.Component;

/**
 * Utility for IBAN-related operations.
 *
 * According to the spec:
 *   ES21 [0000] 0000 00 00000
 *          ^^^^
 *   The bank code is the 4 digits at positions 4–7 (0-based) of the raw IBAN string.
 *
 *   Example: "ES2100000000000000000" → bankCode = 0000 = 0
 */
@Component
public class IbanUtil {

    private static final int BANK_CODE_START = 4;
    private static final int BANK_CODE_END   = 8; // exclusive

    /**
     * Extracts the bank code from a raw IBAN string (no spaces).
     *
     * @param iban raw IBAN (letters + digits, no spaces)
     * @return bank code as an Integer
     * @throws IllegalArgumentException if the IBAN is too short or the extracted code is not numeric
     */
    public Integer extractBankCode(String iban) {
        if (iban == null || iban.length() < BANK_CODE_END) {
            throw new IllegalArgumentException(
                    "IBAN must be at least " + BANK_CODE_END + " characters long to extract bank code");
        }
        String bankCodeStr = iban.substring(BANK_CODE_START, BANK_CODE_END);
        try {
            return Integer.parseInt(bankCodeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Bank code extracted from IBAN (" + bankCodeStr + ") is not numeric");
        }
    }

    /**
     * Basic structural validation: letters + digits, length between 8 and 34.
     */
    public boolean isValidFormat(String iban) {
        return iban != null && iban.matches("^[a-zA-Z0-9]{8,34}$");
    }
}

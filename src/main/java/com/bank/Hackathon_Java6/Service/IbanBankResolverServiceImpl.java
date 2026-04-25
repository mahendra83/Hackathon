package com.bank.Hackathon_Java6.Service;

import org.springframework.stereotype.Service;

@Service
public class IbanBankResolverServiceImpl implements IbanBankResolverService {

    /**
     * Extracts bank code from IBAN.
     * Format: CC##BBBBXXXXXXXXXX
     * CC=Country(2), ##=Check digits(2), BBBB=Bank code(4 digits)
     * Example: ES21 0000 0000 ... -> bankCode = 0000 = 0
     */
    @Override
    public Integer extractBankCodeFromIban(String iban) {
        if (iban == null || iban.length() < 8) {
            return null;
        }
        String stripped = iban.replaceAll("\\s", "");
        String bankCodeStr = stripped.substring(4, 8);
        try {
            return Integer.parseInt(bankCodeStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
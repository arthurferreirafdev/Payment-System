package domain.transaction.valueObjects;

import lombok.Data;

import java.io.Serializable;

@Data
public class Cpf implements Serializable {
    private final String value;

    public Cpf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("CPF must not be null");
        }
        String digits = value.replaceAll("\\D", "");
        if (!isValid(digits)) {
            throw new IllegalArgumentException("Invalid CPF: " + value);
        }
        this.value = digits;
    }

    private static boolean isValid(String digits) {
        if (digits.length() != 11 || digits.chars().allMatch(c -> c == digits.charAt(0))) {
            return false;
        }
        return digits.charAt(9) == checkDigit(digits, 9, 10)
                && digits.charAt(10) == checkDigit(digits, 10, 11);
    }

    private static char checkDigit(String digits, int length, int startWeight) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Character.digit(digits.charAt(i), 10) * (startWeight - i);
        }
        int remainder = sum % 11;
        return (char) ('0' + (remainder < 2 ? 0 : 11 - remainder));
    }
}

package domain.transaction.valueObjects;

import lombok.Data;

import java.io.Serializable;
import java.security.SecureRandom;

@Data
public class AccountNumber implements Serializable {
    private static final long MIN_VALUE = 1_000_000_000L;
    private static final long MAX_VALUE = 9_999_999_999L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final long value;

    public AccountNumber() {
        this.value = MIN_VALUE + (long) (RANDOM.nextDouble() * (MAX_VALUE - MIN_VALUE + 1));
    }

    public AccountNumber(long value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Invalid account number: " + value);
        }
        this.value = value;
    }
}

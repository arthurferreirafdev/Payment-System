package domain.transaction.valueObjects;

import lombok.Data;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Pattern;

@Data
public class Email implements Serializable {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String value;

    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        this.value = normalized;
    }
}

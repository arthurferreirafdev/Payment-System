package domain.transaction.valueObjects;

import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Money implements Serializable {
    private static final String EXCHANGE_RATE_URL = "https://api.frankfurter.app/latest?from=%s&to=%s";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + amount);
        }
        this.amount = normalize(amount, currency);
        this.currency = currency;
    }

    public Money add(Money money) {
        requireMoney(money);
        if (money.currency.equals(currency)) {
            return new Money(amount.add(money.amount), currency);
        }
        return add(money, fetchRate(money.currency, currency));
    }

    public Money add(Money money, BigDecimal rate) {
        requireMoney(money);
        requireRate(rate);
        return new Money(amount.add(convertedAmount(money, rate)), currency);
    }

    public Money subtract(Money money) {
        requireMoney(money);
        if (money.currency.equals(currency)) {
            return subtract(money, BigDecimal.ONE);
        }
        return subtract(money, fetchRate(money.currency, currency));
    }

    public Money subtract(Money money, BigDecimal rate) {
        requireMoney(money);
        requireRate(rate);
        BigDecimal result = amount.subtract(convertedAmount(money, rate));
        if (result.signum() < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Money(result, currency);
    }

    private BigDecimal convertedAmount(Money money, BigDecimal rate) {
        if (money.currency.equals(currency)) {
            return money.amount;
        }
        return money.amount.multiply(rate).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }

    private static void requireRate(BigDecimal rate) {
        if (rate == null || rate.signum() <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
    }

    private static void requireMoney(Money money) {
        if (money == null) {
            throw new IllegalArgumentException("Money must not be null");
        }
    }

    private static BigDecimal normalize(BigDecimal amount, Currency currency) {
        try {
            return amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Amount has invalid precision for " + currency, e);
        }
    }

    private static BigDecimal fetchRate(Currency from, Currency to) {
        String url = String.format(Locale.ROOT, EXCHANGE_RATE_URL, from.getCurrencyCode(), to.getCurrencyCode());
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Exchange rate API returned " + response.statusCode());
            }
            Matcher matcher = Pattern.compile(
                    "\"rates\"\\s*:\\s*\\{\\s*\"" + Pattern.quote(to.getCurrencyCode())
                            + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)")
                    .matcher(response.body());
            if (!matcher.find()) {
                throw new IllegalStateException("Exchange rate not found for " + from + " -> " + to);
            }
            return new BigDecimal(matcher.group(1));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to fetch exchange rate", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching exchange rate", e);
        }
    }

    public boolean isLessThan(Money money){
        return amount.compareTo(money.amount) < 0;
    }
}

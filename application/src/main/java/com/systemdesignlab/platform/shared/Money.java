package com.systemdesignlab.platform.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Canonical monetary value object (ADR-014: "Use a Money value object backed by BigDecimal with
 * explicit currency. Persist authoritative amounts as DECIMAL(19,4) and use HALF_EVEN as the
 * Phase-1 rounding rule. Do not use float/double for authoritative money.").
 *
 * <p>Every constructed {@code Money} normalizes its amount to scale 4 using
 * {@link RoundingMode#HALF_EVEN}, matching the persisted {@code DECIMAL(19,4)} column shape
 * (Architecture v1.3 &sect;9/&sect;12), and rejects amounts whose integer part would not fit in
 * that column (more than 15 integer digits). {@code float}/{@code double} are never used as an
 * authoritative representation anywhere in this class.
 *
 * <p>Currency is an explicit {@link java.util.Currency} -- the standard JDK ISO-4217
 * representation -- rather than an invented supported-currency allow-list; malformed or unknown
 * codes are rejected by the JDK itself via {@link Currency#getInstance(String)}.
 *
 * <p>Only the same-currency arithmetic an established requirement justifies is implemented:
 * {@link #add(Money)} and {@link #subtract(Money)}. Multiplication/division are deliberately not
 * provided here -- ADR-014 does not define rounding semantics for a per-operation scale increase
 * (e.g. multiplying by a non-integer factor), and no current work package needs them. Adding them
 * speculatively would mean inventing rounding behavior ADR-014 does not specify.
 *
 * <p>Cross-currency operations (for example combining a USD amount with a EUR amount) always
 * fail fast with a {@link DomainException} ({@code errorCode() == "CURRENCY_MISMATCH"}); they
 * never silently succeed against one of the two currencies.
 */
public final class Money {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    /** DECIMAL(19,4): 19 total digits, 4 of them fractional, leaves 15 integer digits. */
    private static final int MAX_INTEGER_DIGITS = 15;

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /** Creates a {@code Money}, normalizing {@code amount} to scale 4 with HALF_EVEN rounding. */
    public static Money of(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        BigDecimal normalized = amount.setScale(SCALE, ROUNDING_MODE);
        validatePrecision(normalized);
        return new Money(normalized, currency);
    }

    /** Convenience overload accepting an ISO-4217 currency code (for example {@code "USD"}). */
    public static Money of(BigDecimal amount, String currencyCode) {
        return of(amount, toCurrency(currencyCode));
    }

    /** A zero amount in the given currency. */
    public static Money zero(Currency currency) {
        return of(BigDecimal.ZERO, currency);
    }

    /**
     * Adds {@code other} to this amount. Both operands must share the same currency.
     *
     * @throws DomainException with {@code errorCode() == "CURRENCY_MISMATCH"} if currencies differ
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return of(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts {@code other} from this amount. Both operands must share the same currency.
     *
     * @throws DomainException with {@code errorCode() == "CURRENCY_MISMATCH"} if currencies differ
     */
    public Money subtract(Money other) {
        requireSameCurrency(other);
        return of(this.amount.subtract(other.amount), this.currency);
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("CURRENCY_MISMATCH",
                    "Cannot combine " + this.currency.getCurrencyCode() + " with "
                            + other.currency.getCurrencyCode());
        }
    }

    private static Currency toCurrency(String currencyCode) {
        Objects.requireNonNull(currencyCode, "currencyCode must not be null");
        try {
            return Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new DomainException("INVALID_CURRENCY",
                    "Not a valid ISO-4217 currency code: " + currencyCode, e);
        }
    }

    private static void validatePrecision(BigDecimal normalized) {
        int integerDigits = normalized.precision() - normalized.scale();
        if (integerDigits > MAX_INTEGER_DIGITS) {
            throw new DomainException("INVALID_MONEY_AMOUNT",
                    "Amount exceeds DECIMAL(19,4) precision: " + normalized);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money other)) {
            return false;
        }
        return amount.equals(other.amount) && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount;
    }
}

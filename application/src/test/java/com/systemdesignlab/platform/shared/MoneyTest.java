package com.systemdesignlab.platform.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.jupiter.api.Test;

/**
 * Proves the ADR-014 Money semantics: BigDecimal-backed, explicit currency, scale 4, HALF_EVEN
 * rounding, DECIMAL(19,4) precision limit, and rejection of cross-currency operations. Pure
 * domain logic -- no Spring context.
 */
class MoneyTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");

    // --- construction -------------------------------------------------------------------

    @Test
    void constructsFromAmountAndCurrency() {
        Money money = Money.of(new BigDecimal("10.50"), USD);

        assertThat(money.amount()).isEqualByComparingTo("10.50");
        assertThat(money.currency()).isEqualTo(USD);
    }

    @Test
    void constructsFromAmountAndCurrencyCodeString() {
        Money money = Money.of(new BigDecimal("10.50"), "USD");

        assertThat(money.currency()).isEqualTo(USD);
    }

    @Test
    void zeroFactoryProducesScaleFourZero() {
        Money zero = Money.zero(USD);

        assertThat(zero.amount()).isEqualByComparingTo("0.0000");
        assertThat(zero.amount().scale()).isEqualTo(4);
    }

    // --- invalid construction ------------------------------------------------------------

    @Test
    void rejectsNullAmount() {
        assertThatThrownBy(() -> Money.of(null, USD)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullCurrency() {
        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, (Currency) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsMalformedCurrencyCode() {
        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, "NOTACURRENCY"))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).errorCode()).isEqualTo("INVALID_CURRENCY"));
    }

    @Test
    void rejectsLowercaseCurrencyCode() {
        // java.util.Currency.getInstance is case-sensitive (ISO-4217 codes are uppercase);
        // Money does not invent its own case-normalization on top of the JDK's behavior.
        assertThatThrownBy(() -> Money.of(BigDecimal.TEN, "usd"))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).errorCode()).isEqualTo("INVALID_CURRENCY"));
    }

    // --- precision / scale (DECIMAL(19,4)) ------------------------------------------------

    @Test
    void normalizesToScaleFour() {
        Money money = Money.of(new BigDecimal("10"), USD);

        assertThat(money.amount().scale()).isEqualTo(4);
        assertThat(money.amount()).isEqualByComparingTo("10.0000");
    }

    @Test
    void allowsExactlyFifteenIntegerDigits() {
        // DECIMAL(19,4): 15 integer digits + 4 fractional digits = 19 total -- the boundary.
        Money money = Money.of(new BigDecimal("999999999999999.9999"), USD);

        assertThat(money.amount()).isEqualByComparingTo("999999999999999.9999");
    }

    @Test
    void rejectsSixteenIntegerDigits() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("1000000000000000"), USD))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).errorCode()).isEqualTo("INVALID_MONEY_AMOUNT"));
    }

    // --- rounding (HALF_EVEN, not HALF_UP) ------------------------------------------------

    @Test
    void roundsHalfToEvenWhenDownCandidateIsEven() {
        // 10.00005 is exactly between 10.0000 (even last digit) and 10.0001 (odd last digit).
        // HALF_EVEN picks the even candidate (10.0000); HALF_UP would instead produce 10.0001.
        Money money = Money.of(new BigDecimal("10.00005"), USD);

        assertThat(money.amount()).isEqualByComparingTo("10.0000");
    }

    @Test
    void roundsHalfToEvenWhenUpCandidateIsEven() {
        // 10.00015 is exactly between 10.0001 (odd) and 10.0002 (even); HALF_EVEN rounds up here.
        Money money = Money.of(new BigDecimal("10.00015"), USD);

        assertThat(money.amount()).isEqualByComparingTo("10.0002");
    }

    // --- equality --------------------------------------------------------------------------

    @Test
    void equalWhenAmountAndCurrencyMatchRegardlessOfInputScale() {
        Money fromTwoDecimals = Money.of(new BigDecimal("10.50"), USD);
        Money fromFourDecimals = Money.of(new BigDecimal("10.5000"), USD);

        assertThat(fromTwoDecimals).isEqualTo(fromFourDecimals);
        assertThat(fromTwoDecimals.hashCode()).isEqualTo(fromFourDecimals.hashCode());
    }

    @Test
    void notEqualWhenCurrencyDiffers() {
        Money usd = Money.of(BigDecimal.TEN, USD);
        Money eur = Money.of(BigDecimal.TEN, EUR);

        assertThat(usd).isNotEqualTo(eur);
    }

    @Test
    void notEqualWhenAmountDiffers() {
        Money ten = Money.of(BigDecimal.TEN, USD);
        Money eleven = Money.of(BigDecimal.valueOf(11), USD);

        assertThat(ten).isNotEqualTo(eleven);
    }

    // --- same-currency arithmetic ------------------------------------------------------------

    @Test
    void addsSameCurrencyAmounts() {
        Money result = Money.of(new BigDecimal("10.00"), USD).add(Money.of(new BigDecimal("5.25"), USD));

        assertThat(result).isEqualTo(Money.of(new BigDecimal("15.25"), USD));
    }

    @Test
    void subtractsSameCurrencyAmounts() {
        Money result = Money.of(new BigDecimal("10.00"), USD).subtract(Money.of(new BigDecimal("5.25"), USD));

        assertThat(result).isEqualTo(Money.of(new BigDecimal("4.75"), USD));
    }

    // --- cross-currency rejection ------------------------------------------------------------

    @Test
    void rejectsAddingDifferentCurrencies() {
        Money usd = Money.of(BigDecimal.TEN, USD);
        Money eur = Money.of(BigDecimal.TEN, EUR);

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).errorCode()).isEqualTo("CURRENCY_MISMATCH"));
    }

    @Test
    void rejectsSubtractingDifferentCurrencies() {
        Money usd = Money.of(BigDecimal.TEN, USD);
        Money eur = Money.of(BigDecimal.TEN, EUR);

        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(DomainException.class)
                .satisfies(e -> assertThat(((DomainException) e).errorCode()).isEqualTo("CURRENCY_MISMATCH"));
    }

    // --- negative values (not forbidden by ADR-014) -------------------------------------------

    @Test
    void allowsNegativeAmounts() {
        Money negative = Money.of(new BigDecimal("-10.50"), USD);

        assertThat(negative.amount()).isEqualByComparingTo("-10.50");
    }

    @Test
    void subtractingLargerFromSmallerProducesNegativeResult() {
        Money result = Money.of(new BigDecimal("5.00"), USD).subtract(Money.of(new BigDecimal("10.00"), USD));

        assertThat(result.amount()).isEqualByComparingTo("-5.00");
    }

    // --- toString -------------------------------------------------------------------------

    @Test
    void toStringIncludesCurrencyCodeAndAmount() {
        Money money = Money.of(new BigDecimal("10.50"), USD);

        assertThat(money.toString()).isEqualTo("USD 10.5000");
    }
}

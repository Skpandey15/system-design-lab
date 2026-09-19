package com.systemdesignlab.platform.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Proves the ADR-010 stable-domain-error vocabulary primitive: a machine-readable, stable
 * {@code errorCode} distinct from the human-readable message, with deterministic semantics.
 */
class DomainExceptionTest {

    @Test
    void exposesTheStableErrorCode() {
        DomainException exception = new DomainException("CURRENCY_MISMATCH", "USD and EUR cannot combine");

        assertThat(exception.errorCode()).isEqualTo("CURRENCY_MISMATCH");
        assertThat(exception.getMessage()).isEqualTo("USD and EUR cannot combine");
    }

    @Test
    void supportsAnOptionalCause() {
        IllegalArgumentException cause = new IllegalArgumentException("root cause");

        DomainException exception = new DomainException("INVALID_CURRENCY", "bad code", cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void rejectsNullErrorCode() {
        assertThatThrownBy(() -> new DomainException(null, "message")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsBlankErrorCode() {
        assertThatThrownBy(() -> new DomainException("   ", "message")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isARuntimeExceptionNotACheckedException() {
        assertThat(new DomainException("CODE", "message")).isInstanceOf(RuntimeException.class);
    }
}

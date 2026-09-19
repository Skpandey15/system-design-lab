package com.systemdesignlab.platform.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Proves the ADR-010 correlation-id primitive: deterministic value semantics, generation, and
 * acceptance of externally supplied (not necessarily UUID-shaped) tokens.
 */
class CorrelationIdTest {

    @Test
    void generateProducesANonBlankValue() {
        CorrelationId id = CorrelationId.generate();

        assertThat(id.value()).isNotBlank();
    }

    @Test
    void generateProducesDistinctValues() {
        assertThat(CorrelationId.generate()).isNotEqualTo(CorrelationId.generate());
    }

    @Test
    void ofAcceptsAnyNonBlankExternalToken() {
        // Correlation ids may originate from an upstream caller and are not required to be UUIDs.
        CorrelationId id = CorrelationId.of("upstream-request-id-12345");

        assertThat(id.value()).isEqualTo("upstream-request-id-12345");
    }

    @Test
    void rejectsNullValue() {
        assertThatThrownBy(() -> CorrelationId.of(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsBlankValue() {
        assertThatThrownBy(() -> CorrelationId.of("   ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalWhenValueIsEqual() {
        assertThat(CorrelationId.of("abc")).isEqualTo(CorrelationId.of("abc"));
    }

    @Test
    void hashCodeConsistentWithEquals() {
        assertThat(CorrelationId.of("abc").hashCode()).isEqualTo(CorrelationId.of("abc").hashCode());
    }

    @Test
    void toStringIsTheRawValue() {
        assertThat(CorrelationId.of("abc").toString()).isEqualTo("abc");
    }
}

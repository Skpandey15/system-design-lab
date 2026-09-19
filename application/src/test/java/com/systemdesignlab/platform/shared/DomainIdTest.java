package com.systemdesignlab.platform.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Proves the opaque-immutable-identifier semantics required by Architecture v1.3 &sect;9:
 * immutable, deterministic equality/hashCode, useful string representation, null/invalid-value
 * protection.
 */
class DomainIdTest {

    @Test
    void newIdGeneratesADistinctIdentifierEachTime() {
        DomainId first = DomainId.newId();
        DomainId second = DomainId.newId();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void ofUuidWrapsTheGivenValue() {
        UUID uuid = UUID.randomUUID();

        DomainId id = DomainId.of(uuid);

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void ofStringParsesACanonicalUuidString() {
        UUID uuid = UUID.randomUUID();

        DomainId id = DomainId.of(uuid.toString());

        assertThat(id.value()).isEqualTo(uuid);
    }

    @Test
    void rejectsNullUuid() {
        assertThatThrownBy(() -> DomainId.of((UUID) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsNullString() {
        assertThatThrownBy(() -> DomainId.of((String) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void rejectsMalformedString() {
        assertThatThrownBy(() -> DomainId.of("not-a-uuid")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equalWhenBackingUuidIsEqual() {
        UUID uuid = UUID.randomUUID();

        assertThat(DomainId.of(uuid)).isEqualTo(DomainId.of(uuid.toString()));
    }

    @Test
    void hashCodeConsistentWithEquals() {
        UUID uuid = UUID.randomUUID();

        assertThat(DomainId.of(uuid).hashCode()).isEqualTo(DomainId.of(uuid.toString()).hashCode());
    }

    @Test
    void toStringIsTheCanonicalUuidString() {
        UUID uuid = UUID.randomUUID();

        assertThat(DomainId.of(uuid).toString()).isEqualTo(uuid.toString());
    }
}

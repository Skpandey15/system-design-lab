package com.systemdesignlab.platform.shared;

import java.util.Objects;
import java.util.UUID;

/**
 * Opaque, immutable domain identifier.
 *
 * <p>Architecture v1.3 &sect;9 ("Transaction and Consistency Model") specifies identifier
 * semantics as "opaque immutable IDs; UUID/UUIDv7 acceptable implementation choice". This class
 * uses standard {@link UUID} (JDK-native, zero extra dependencies): the architecture explicitly
 * accepts either UUID or UUIDv7, so plain {@code UUID.randomUUID()} satisfies that choice without
 * introducing a third-party time-ordered-UUID implementation.
 *
 * <p>Deliberately generic rather than one class per business entity ({@code OrderId},
 * {@code ProductId}, ...): WP-03 provides only the smallest shared abstraction the architecture
 * requires. Business modules may introduce their own strongly-typed identifiers, backed by this
 * class, once a concrete work package actually needs them.
 */
public final class DomainId {

    private final UUID value;

    private DomainId(UUID value) {
        this.value = value;
    }

    /** Generates a new, random identifier. */
    public static DomainId newId() {
        return new DomainId(UUID.randomUUID());
    }

    /** Wraps an existing {@link UUID} value. */
    public static DomainId of(UUID value) {
        Objects.requireNonNull(value, "value must not be null");
        return new DomainId(value);
    }

    /** Parses a canonical UUID string representation. */
    public static DomainId of(String value) {
        Objects.requireNonNull(value, "value must not be null");
        try {
            return new DomainId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("not a valid identifier: " + value, e);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomainId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

package com.systemdesignlab.platform.shared;

import java.util.Objects;

/**
 * Stable, machine-readable domain-level failure (ADR-010).
 *
 * <p>Carries an {@code errorCode} intended to stay stable across releases so a later
 * API-boundary work package (WP-14) can map it into the client-facing error contract
 * ({@code code}, {@code message}, {@code correlationId}, {@code timestamp}, {@code fieldErrors})
 * without the domain layer knowing anything about HTTP status codes, response envelopes, or
 * REST. This class intentionally does not know about any of those transport concepts, and does
 * not itself carry a correlation id or timestamp -- attaching those to a response is the API
 * boundary's job, not the domain's.
 *
 * <p>Each throwing site defines its own error-code string rather than selecting from one shared
 * enum owned by this package: a single shared taxonomy would recouple every bounded context
 * through one file every time a new failure is introduced, which is exactly what the module
 * boundaries elsewhere in this codebase are designed to avoid.
 */
public final class DomainException extends RuntimeException {

    private final String errorCode;

    public DomainException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public DomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        Objects.requireNonNull(errorCode, "errorCode must not be null");
        if (errorCode.isBlank()) {
            throw new IllegalArgumentException("errorCode must not be blank");
        }
        this.errorCode = errorCode;
    }

    /**
     * The stable, machine-readable identifier for this failure (for example
     * {@code "CURRENCY_MISMATCH"}). Distinct from {@link #getMessage()}, which is a
     * human-readable, non-contractual detail string.
     */
    public String errorCode() {
        return errorCode;
    }
}

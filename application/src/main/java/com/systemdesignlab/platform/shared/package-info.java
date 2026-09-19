/**
 * Cross-cutting primitives shared across bounded contexts.
 *
 * <p>WP-03 populates this package with the shared domain primitives required by ADR-010 and
 * ADR-014: {@link com.systemdesignlab.platform.shared.DomainId} (opaque identifiers),
 * {@link com.systemdesignlab.platform.shared.Money} (the canonical monetary value object),
 * {@link com.systemdesignlab.platform.shared.CorrelationId} (the correlation/audit primitive),
 * and {@link com.systemdesignlab.platform.shared.DomainException} (the stable domain-error base
 * type). Per the Phase-1 implementation plan, this package must stay conservative: only
 * genuinely cross-cutting technical primitives belong here, and only once a concrete work
 * package actually needs them. It must never become a dumping ground for module-specific domain
 * models, repositories, or services &mdash; those belong inside their owning bounded context.
 */
package com.systemdesignlab.platform.shared;

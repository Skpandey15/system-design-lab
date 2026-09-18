/**
 * Cross-cutting primitives shared across bounded contexts.
 *
 * <p>Deliberately empty at WP-01 (architecture skeleton only). Per the Phase-1 implementation
 * plan, this package must stay conservative: only genuinely cross-cutting technical
 * primitives (for example identifiers, correlation context) belong here, and only once a
 * concrete work package actually needs them. It must never become a dumping ground for
 * module-specific domain models, repositories, or services &mdash; those belong inside their
 * owning bounded context.
 */
package com.systemdesignlab.platform.shared;

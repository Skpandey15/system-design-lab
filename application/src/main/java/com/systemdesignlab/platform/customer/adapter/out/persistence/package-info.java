/**
 * Outbound persistence adapters for the Customer bounded context.
 *
 * <p>Implements {@link com.systemdesignlab.platform.customer.port.out} against this module's
 * own PostgreSQL schema only. No other bounded context may depend on classes in this
 * package &mdash; enforced by the cross-module persistence isolation architecture fitness
 * test per ADR-002 (DDD Bounded Contexts) and ADR-018 (Schema-per-Module Isolation).
 */
@HexagonalLayer("adapter.out.persistence")
package com.systemdesignlab.platform.customer.adapter.out.persistence;

import com.systemdesignlab.platform.shared.HexagonalLayer;

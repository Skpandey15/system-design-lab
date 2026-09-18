/**
 * Domain layer for the Payment bounded context: Payment intent/authorization record and payment lifecycle, sequenced outside the order's DB transaction.
 *
 * <p>Contains the module's domain model (entities, value objects, invariants) and nothing
 * else. This layer must have no dependency on Spring, JPA/persistence, REST, or any adapter
 * &mdash; enforced by the domain-independence architecture fitness test
 * ({@code com.systemdesignlab.platform.architecture.ModularMonolithArchitectureTest}) per
 * ADR-003 (Hexagonal Architecture).
 */
@HexagonalLayer("domain")
package com.systemdesignlab.platform.payment.domain;

import com.systemdesignlab.platform.shared.HexagonalLayer;

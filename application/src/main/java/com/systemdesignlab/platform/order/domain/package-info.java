/**
 * Domain layer for the Order bounded context: Order aggregate, order lifecycle, and orchestration of the Phase-1 order use case.
 *
 * <p>Contains the module's domain model (entities, value objects, invariants) and nothing
 * else. This layer must have no dependency on Spring, JPA/persistence, REST, or any adapter
 * &mdash; enforced by the domain-independence architecture fitness test
 * ({@code com.systemdesignlab.platform.architecture.ModularMonolithArchitectureTest}) per
 * ADR-003 (Hexagonal Architecture).
 */
@HexagonalLayer("domain")
package com.systemdesignlab.platform.order.domain;

import com.systemdesignlab.platform.shared.HexagonalLayer;

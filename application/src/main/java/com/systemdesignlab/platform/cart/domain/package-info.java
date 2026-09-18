/**
 * Domain layer for the Cart bounded context: Customer shopping cart and cart items; captures an advisory price at add-time.
 *
 * <p>Contains the module's domain model (entities, value objects, invariants) and nothing
 * else. This layer must have no dependency on Spring, JPA/persistence, REST, or any adapter
 * &mdash; enforced by the domain-independence architecture fitness test
 * ({@code com.systemdesignlab.platform.architecture.ModularMonolithArchitectureTest}) per
 * ADR-003 (Hexagonal Architecture).
 */
@HexagonalLayer("domain")
package com.systemdesignlab.platform.cart.domain;

import com.systemdesignlab.platform.shared.HexagonalLayer;

/**
 * System Design Lab &mdash; Phase 1 Order &amp; Payment Platform.
 *
 * <p>A single-deployable modular monolith composed of six DDD bounded contexts
 * ({@link com.systemdesignlab.platform.customer}, {@link com.systemdesignlab.platform.catalog},
 * {@link com.systemdesignlab.platform.cart}, {@link com.systemdesignlab.platform.order},
 * {@link com.systemdesignlab.platform.inventory}, {@link com.systemdesignlab.platform.payment})
 * plus {@link com.systemdesignlab.platform.shared} cross-cutting primitives.
 *
 * <p>Module boundaries are logical/code boundaries, not network boundaries, and are enforced
 * by the architecture fitness tests in {@code com.systemdesignlab.platform.architecture}
 * (test sources). See Architecture v1.2 and ADR-001 (Modular Monolith), ADR-002 (DDD Bounded
 * Contexts), ADR-003 (Hexagonal Architecture), ADR-008 (Architecture Fitness Functions).
 */
package com.systemdesignlab.platform;

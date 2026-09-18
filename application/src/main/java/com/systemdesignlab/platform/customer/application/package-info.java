/**
 * Application layer for the Customer bounded context.
 *
 * <p>Hosts use-case orchestration services that coordinate the domain model through
 * {@link com.systemdesignlab.platform.customer.port.out} output ports. Must not depend on
 * outbound adapter implementations directly, and must not depend on any inbound adapter
 * either &mdash; enforced by the application-isolation and adapter-direction architecture
 * fitness tests per ADR-003 (Hexagonal Architecture).
 */
@HexagonalLayer("application")
package com.systemdesignlab.platform.customer.application;

import com.systemdesignlab.platform.shared.HexagonalLayer;

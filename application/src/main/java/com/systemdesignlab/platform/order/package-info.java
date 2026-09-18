/**
 * Order bounded context: Order aggregate, order lifecycle, and orchestration of the Phase-1 order use case.
 *
 * <p>Structured as a Hexagonal Architecture module:
 * <ul>
 *   <li>{@code domain} &mdash; framework-independent domain model</li>
 *   <li>{@code application} &mdash; use-case orchestration</li>
 *   <li>{@code port.in} / {@code port.out} &mdash; published application contracts</li>
 *   <li>{@code adapter.in.rest} / {@code adapter.out.persistence} &mdash; framework-facing
 *       inbound/outbound implementations</li>
 * </ul>
 *
 * <p>Other bounded contexts must not depend on this module's {@code adapter.out.persistence}
 * classes; cross-module collaboration goes through {@code port.out} contracts only.
 * See Architecture v1.2 &sect;6-7 and ADR-001, ADR-002, ADR-003, ADR-018.
 */
package com.systemdesignlab.platform.order;

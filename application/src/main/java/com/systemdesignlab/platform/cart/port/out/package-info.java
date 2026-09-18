/**
 * Output ports for the Cart bounded context.
 *
 * <p>Interfaces the application layer depends on for persistence and other outbound
 * collaboration. Outbound adapters under
 * {@link com.systemdesignlab.platform.cart.adapter.out.persistence} implement these
 * interfaces; the application layer never depends on a concrete adapter implementation
 * (ADR-003 Hexagonal Architecture).
 */
@HexagonalLayer("port.out")
package com.systemdesignlab.platform.cart.port.out;

import com.systemdesignlab.platform.shared.HexagonalLayer;

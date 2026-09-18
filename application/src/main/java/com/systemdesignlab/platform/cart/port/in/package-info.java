/**
 * Input ports (use-case interfaces) for the Cart bounded context.
 *
 * <p>Inbound adapters (for example REST controllers under
 * {@link com.systemdesignlab.platform.cart.adapter.in.rest}) depend on these interfaces
 * to invoke application use cases; they never call application services directly.
 */
@HexagonalLayer("port.in")
package com.systemdesignlab.platform.cart.port.in;

import com.systemdesignlab.platform.shared.HexagonalLayer;

/**
 * Input ports (use-case interfaces) for the Customer bounded context.
 *
 * <p>Inbound adapters (for example REST controllers under
 * {@link com.systemdesignlab.platform.customer.adapter.in.rest}) depend on these interfaces
 * to invoke application use cases; they never call application services directly.
 */
@HexagonalLayer("port.in")
package com.systemdesignlab.platform.customer.port.in;

import com.systemdesignlab.platform.shared.HexagonalLayer;

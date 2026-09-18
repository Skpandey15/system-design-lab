/**
 * Input ports (use-case interfaces) for the Payment bounded context.
 *
 * <p>Inbound adapters (for example REST controllers under
 * {@link com.systemdesignlab.platform.payment.adapter.in.rest}) depend on these interfaces
 * to invoke application use cases; they never call application services directly.
 */
@HexagonalLayer("port.in")
package com.systemdesignlab.platform.payment.port.in;

import com.systemdesignlab.platform.shared.HexagonalLayer;

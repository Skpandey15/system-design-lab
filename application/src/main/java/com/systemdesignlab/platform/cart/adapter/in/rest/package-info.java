/**
 * Inbound REST adapters for the Cart bounded context.
 *
 * <p>Controllers in this package must depend only on
 * {@link com.systemdesignlab.platform.cart.port.in} use cases, never on this module's
 * (or any other module's) persistence adapters directly &mdash; enforced by the
 * controller-boundary architecture fitness test.
 */
@HexagonalLayer("adapter.in.rest")
package com.systemdesignlab.platform.cart.adapter.in.rest;

import com.systemdesignlab.platform.shared.HexagonalLayer;

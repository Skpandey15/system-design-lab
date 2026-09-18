/**
 * Inbound REST adapters for the Inventory bounded context.
 *
 * <p>Controllers in this package must depend only on
 * {@link com.systemdesignlab.platform.inventory.port.in} use cases, never on this module's
 * (or any other module's) persistence adapters directly &mdash; enforced by the
 * controller-boundary architecture fitness test.
 */
@HexagonalLayer("adapter.in.rest")
package com.systemdesignlab.platform.inventory.adapter.in.rest;

import com.systemdesignlab.platform.shared.HexagonalLayer;

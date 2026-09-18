package com.systemdesignlab.archunitfixtures.controllerboundary.outboundadapterviolation.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.outboundadapterviolation.adapter.out.messaging.FixtureMessagingAdapter;

/**
 * Architecture-test fixture: a "REST adapter" class that (incorrectly) depends directly on a
 * non-persistence outbound adapter, bypassing the module's input port. Used only to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} detects this violation
 * (REST -&gt; other outbound adapter), not only the persistence-specific case.
 */
public class BadControllerUsingOtherOutboundAdapter {

    private final FixtureMessagingAdapter messagingAdapter = new FixtureMessagingAdapter();

    public void handle() {
        messagingAdapter.send();
    }
}

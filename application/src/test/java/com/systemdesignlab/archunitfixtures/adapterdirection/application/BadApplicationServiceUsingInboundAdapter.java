package com.systemdesignlab.archunitfixtures.adapterdirection.application;

import com.systemdesignlab.archunitfixtures.adapterdirection.adapter.in.FixtureInboundAdapter;

/**
 * Architecture-test fixture: an "application" class that (incorrectly) depends on an inbound
 * adapter. Used only to prove {@code ModuleArchitectureRules.adapterDirection()} detects a
 * violation that the narrower {@code applicationIsolation()} rule (outbound-only) would not.
 */
public class BadApplicationServiceUsingInboundAdapter {

    private final FixtureInboundAdapter adapter = new FixtureInboundAdapter();

    public void run() {
        adapter.touch();
    }
}

package com.systemdesignlab.archunitfixtures.applicationisolation.application;

import com.systemdesignlab.archunitfixtures.applicationisolation.adapter.out.FixtureOutboundAdapter;

/**
 * Architecture-test fixture: an "application" class that (incorrectly) depends directly on an
 * outbound adapter. Used only to prove {@code ModuleArchitectureRules.applicationIsolation()}
 * detects this violation.
 */
public class BadApplicationServiceUsingOutboundAdapter {

    private final FixtureOutboundAdapter adapter = new FixtureOutboundAdapter();

    public void run() {
        adapter.touch();
    }
}

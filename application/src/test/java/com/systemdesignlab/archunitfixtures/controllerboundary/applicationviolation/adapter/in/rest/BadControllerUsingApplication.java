package com.systemdesignlab.archunitfixtures.controllerboundary.applicationviolation.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.applicationviolation.application.FixtureApplicationService;

/**
 * Architecture-test fixture: a "REST adapter" class that (incorrectly) depends directly on an
 * application service, bypassing the module's input port. Used only to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} detects this violation
 * (REST -&gt; application implementation).
 */
public class BadControllerUsingApplication {

    private final FixtureApplicationService service = new FixtureApplicationService();

    public void handle() {
        service.run();
    }
}

package com.systemdesignlab.archunitfixtures.controllerboundary.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.adapter.out.persistence.FixtureRepository;

/**
 * Architecture-test fixture: a "REST adapter" class that (incorrectly) depends directly on a
 * persistence adapter, skipping the application input port. Used only to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} detects this violation.
 */
public class BadController {

    private final FixtureRepository repository = new FixtureRepository();

    public void handle() {
        repository.find();
    }
}

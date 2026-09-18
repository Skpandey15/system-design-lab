package com.systemdesignlab.archunitfixtures.controllerboundary.persistenceviolation.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.persistenceviolation.adapter.out.persistence.FixtureRepository;

/**
 * Architecture-test fixture: a "REST adapter" class that (incorrectly) depends directly on a
 * persistence adapter, skipping the module's input port. Used only to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} detects this violation
 * (REST -&gt; persistence adapter).
 */
public class BadControllerUsingPersistence {

    private final FixtureRepository repository = new FixtureRepository();

    public void handle() {
        repository.find();
    }
}

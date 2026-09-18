package com.systemdesignlab.archunitfixtures.controllerboundary.validcase.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.validcase.port.in.FixtureUseCase;

/**
 * Architecture-test fixture: a "REST adapter" class that correctly depends only on the
 * module's input port. Used to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} does not flag the valid shape
 * (REST -&gt; port.in).
 */
public class GoodControllerUsingPortIn {

    private final FixtureUseCase useCase;

    public GoodControllerUsingPortIn(FixtureUseCase useCase) {
        this.useCase = useCase;
    }

    public void handle() {
        useCase.execute();
    }
}

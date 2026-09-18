package com.systemdesignlab.archunitfixtures.controllerboundary.domainviolation.adapter.in.rest;

import com.systemdesignlab.archunitfixtures.controllerboundary.domainviolation.domain.FixtureDomainEntity;

/**
 * Architecture-test fixture: a "REST adapter" class that (incorrectly) depends directly on a
 * domain class, bypassing the module's input port. Used only to prove
 * {@code ModuleArchitectureRules.controllerBoundary()} detects this violation
 * (REST -&gt; domain implementation).
 */
public class BadControllerUsingDomain {

    private final FixtureDomainEntity entity = new FixtureDomainEntity();

    public void handle() {
        entity.mutate();
    }
}

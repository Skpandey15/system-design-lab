package com.systemdesignlab.archunitfixtures.floatingpointmoney.domain;

/**
 * Architecture-test fixture: a "domain" class that (incorrectly) uses {@code double} for a
 * monetary-shaped field. Used only to prove
 * {@code ModuleArchitectureRules.noFloatingPointDomainMonetaryRepresentation()} detects this
 * violation; never referenced from production code.
 */
public class BadDomainEntityUsingDouble {

    private double amount;
}

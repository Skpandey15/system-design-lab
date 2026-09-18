package com.systemdesignlab.archunitfixtures.domainindependence.domain;

import org.springframework.stereotype.Component;

/**
 * Architecture-test fixture: a "domain" class that (incorrectly) depends on Spring. Used only
 * to prove {@code ModuleArchitectureRules.domainIndependence()} detects this violation; never
 * referenced from production code.
 */
@Component
public class BadDomainEntityUsingSpring {
}

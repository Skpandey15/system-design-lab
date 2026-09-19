package com.systemdesignlab.archunitfixtures.sharedkernelframeworkindependence.shared;

import org.springframework.stereotype.Component;

/**
 * Architecture-test fixture: a "shared kernel" class that (incorrectly) depends on Spring. Used
 * only to prove {@code ModuleArchitectureRules.sharedKernelFrameworkIndependence()} detects this
 * violation; never referenced from production code.
 */
@Component
public class BadSharedClassUsingSpring {
}

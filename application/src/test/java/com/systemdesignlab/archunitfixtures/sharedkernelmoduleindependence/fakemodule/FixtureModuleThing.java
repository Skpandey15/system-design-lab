package com.systemdesignlab.archunitfixtures.sharedkernelmoduleindependence.fakemodule;

/**
 * Architecture-test fixture: stands in for a business module's class. Used only to prove
 * {@code ModuleArchitectureRules.sharedKernelModuleIndependence(String)} detects a shared-kernel
 * class depending on a sibling module; never referenced from production code.
 */
public class FixtureModuleThing {
}

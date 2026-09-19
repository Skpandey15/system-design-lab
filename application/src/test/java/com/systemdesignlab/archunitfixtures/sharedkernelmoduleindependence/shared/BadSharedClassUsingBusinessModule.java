package com.systemdesignlab.archunitfixtures.sharedkernelmoduleindependence.shared;

import com.systemdesignlab.archunitfixtures.sharedkernelmoduleindependence.fakemodule.FixtureModuleThing;

/**
 * Architecture-test fixture: a "shared kernel" class that (incorrectly) depends on a sibling
 * business module. Used only to prove
 * {@code ModuleArchitectureRules.sharedKernelModuleIndependence(String)} detects this violation;
 * never referenced from production code.
 */
public class BadSharedClassUsingBusinessModule {

    FixtureModuleThing thing;
}

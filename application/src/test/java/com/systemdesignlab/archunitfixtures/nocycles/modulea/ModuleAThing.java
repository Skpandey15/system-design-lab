package com.systemdesignlab.archunitfixtures.nocycles.modulea;

import com.systemdesignlab.archunitfixtures.nocycles.moduleb.ModuleBThing;

/**
 * Architecture-test fixture: half of a deliberate two-module cycle
 * ({@code modulea -> moduleb -> modulea}). Used only to prove
 * {@code ModuleArchitectureRules.noCyclicModuleDependencies(String)} detects module cycles
 * (analogous to a real cycle between, for example, order and inventory).
 */
public class ModuleAThing {

    private final ModuleBThing other = new ModuleBThing();

    public void run() {
        other.linkBackTo(this);
        other.run();
    }
}

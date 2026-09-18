package com.systemdesignlab.archunitfixtures.nocycles.moduleb;

import com.systemdesignlab.archunitfixtures.nocycles.modulea.ModuleAThing;

/**
 * Architecture-test fixture: the other half of a deliberate two-module cycle
 * ({@code modulea -> moduleb -> modulea}). See {@code ModuleAThing}.
 */
public class ModuleBThing {

    private ModuleAThing other;

    public void linkBackTo(ModuleAThing other) {
        this.other = other;
    }

    public void run() {
    }
}

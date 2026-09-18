package com.systemdesignlab.archunitfixtures.crossmodulepersistence.modulea.adapter.out.persistence;

import com.systemdesignlab.archunitfixtures.crossmodulepersistence.moduleb.adapter.out.persistence.ModuleBRepository;

/**
 * Architecture-test fixture: module "modulea"'s persistence adapter (incorrectly) depending on
 * module "moduleb"'s persistence adapter. Used only to prove
 * {@code ModuleArchitectureRules.crossModulePersistenceIsolation(String)} detects this
 * violation (analogous to the real order/inventory/payment modules under
 * {@code com.systemdesignlab.platform}).
 */
public class ModuleARepository {

    private final ModuleBRepository foreignRepository = new ModuleBRepository();

    public void save() {
        foreignRepository.save();
    }
}

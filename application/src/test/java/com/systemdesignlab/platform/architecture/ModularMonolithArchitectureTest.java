package com.systemdesignlab.platform.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Production architecture fitness functions for the modular monolith, run as part of the
 * ordinary {@code ./gradlew test} build (ADR-008: architecture as executable policy).
 *
 * <p>Only compiled {@code main} classes are analyzed ({@link ImportOption.DoNotIncludeTests}
 * excludes test-source output by location), so the fixtures that prove each rule can actually
 * detect a violation &mdash; see {@link ArchitectureRulesSelfTest} &mdash; never affect this
 * suite, regardless of the package names they use.
 */
@AnalyzeClasses(packages = "com.systemdesignlab.platform", importOptions = ImportOption.DoNotIncludeTests.class)
class ModularMonolithArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_must_be_independent = ModuleArchitectureRules.domainIndependence();

    @ArchTest
    static final ArchRule application_layer_must_not_depend_on_outbound_adapters =
            ModuleArchitectureRules.applicationIsolation();

    @ArchTest
    static final ArchRule core_code_must_not_depend_back_on_adapters =
            ModuleArchitectureRules.adapterDirection();

    @ArchTest
    static final ArchRule modules_must_not_access_other_modules_persistence =
            ModuleArchitectureRules.crossModulePersistenceIsolation("com.systemdesignlab.platform");

    @ArchTest
    static final ArchRule modules_must_be_free_of_cyclic_dependencies =
            ModuleArchitectureRules.noCyclicModuleDependencies("com.systemdesignlab.platform");

    @ArchTest
    static final ArchRule rest_adapters_must_not_depend_on_persistence_adapters =
            ModuleArchitectureRules.controllerBoundary();
}

package com.systemdesignlab.platform.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Proves that each {@link ModuleArchitectureRules} rule actually detects the violation it
 * claims to guard against, using deliberately-broken fixtures under
 * {@code com.systemdesignlab.archunitfixtures} (test sources only).
 *
 * <p>Those fixtures are never imported by {@link ModularMonolithArchitectureTest} (which
 * analyzes only {@code com.systemdesignlab.platform} main classes), so without this class a
 * green {@link ModularMonolithArchitectureTest} would prove nothing beyond "no production code
 * exists yet". These tests demonstrate the rule mechanisms themselves are sound.
 */
class ArchitectureRulesSelfTest {

    @Test
    void domainIndependenceRuleDetectsSpringDependencyInDomain() {
        assertViolationDetected(
                ModuleArchitectureRules.domainIndependence(),
                "com.systemdesignlab.archunitfixtures.domainindependence");
    }

    @Test
    void applicationIsolationRuleDetectsDependencyOnOutboundAdapter() {
        assertViolationDetected(
                ModuleArchitectureRules.applicationIsolation(),
                "com.systemdesignlab.archunitfixtures.applicationisolation");
    }

    @Test
    void adapterDirectionRuleDetectsApplicationDependingOnInboundAdapter() {
        // Deliberately an application -> adapter.in dependency: applicationIsolation() only
        // covers adapter.out, so this proves adapterDirection() catches the broader case.
        assertViolationDetected(
                ModuleArchitectureRules.adapterDirection(),
                "com.systemdesignlab.archunitfixtures.adapterdirection");
    }

    @Test
    void crossModulePersistenceIsolationRuleDetectsForeignModuleAccess() {
        String basePackage = "com.systemdesignlab.archunitfixtures.crossmodulepersistence";
        assertViolationDetected(ModuleArchitectureRules.crossModulePersistenceIsolation(basePackage), basePackage);
    }

    @Test
    void noCyclicModuleDependenciesRuleDetectsATwoModuleCycle() {
        String basePackage = "com.systemdesignlab.archunitfixtures.nocycles";
        assertViolationDetected(ModuleArchitectureRules.noCyclicModuleDependencies(basePackage), basePackage);
    }

    @Test
    void controllerBoundaryRuleDetectsRestAdapterDependingOnPersistenceAdapter() {
        assertViolationDetected(
                ModuleArchitectureRules.controllerBoundary(),
                "com.systemdesignlab.archunitfixtures.controllerboundary");
    }

    private static void assertViolationDetected(ArchRule rule, String fixturePackage) {
        JavaClasses fixtureClasses = new ClassFileImporter().importPackages(fixturePackage);
        assertThrows(AssertionError.class, () -> rule.check(fixtureClasses),
                () -> "expected rule to detect a violation in fixture package " + fixturePackage);
    }
}

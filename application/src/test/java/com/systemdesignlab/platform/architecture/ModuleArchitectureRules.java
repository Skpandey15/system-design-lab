package com.systemdesignlab.platform.architecture;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Reusable architecture fitness rules for the Phase-1 modular monolith (ADR-001, ADR-002,
 * ADR-003, ADR-008).
 *
 * <p>Each rule is defined exactly once here and applied twice:
 * <ul>
 *   <li>against the real production package ({@code com.systemdesignlab.platform}) in
 *       {@link ModularMonolithArchitectureTest}, where it is expected to pass, and</li>
 *   <li>against deliberately-broken fixtures in {@link ArchitectureRulesSelfTest}, where it is
 *       expected to fail &mdash; proving the rule can actually catch the violation it names.</li>
 * </ul>
 */
public final class ModuleArchitectureRules {

    private ModuleArchitectureRules() {
    }

    /**
     * Rule A &mdash; domain independence: classes residing in {@code ..domain..} must not
     * depend on Spring, JPA, or any adapter.
     */
    public static ArchRule domainIndependence() {
        return noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "..adapter..")
                .as("classes residing in '..domain..' must not depend on Spring, JPA, or adapters")
                .because("the domain layer must remain independent of frameworks and infrastructure (ADR-003)");
    }

    /**
     * Rule B &mdash; application isolation: classes residing in {@code ..application..} must
     * not depend directly on {@code ..adapter.out..}.
     */
    public static ArchRule applicationIsolation() {
        return noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
                .as("classes residing in '..application..' must not depend on '..adapter.out..'")
                .because("application code must depend on output ports, not concrete outbound adapters (ADR-003)");
    }

    /**
     * Rule C &mdash; adapter direction: core code (domain or application) must never depend
     * back on any adapter, inbound or outbound. Broader than {@link #applicationIsolation()},
     * which only covers the application-to-outbound-adapter case.
     */
    public static ArchRule adapterDirection() {
        return noClasses()
                .that().resideInAnyPackage("..domain..", "..application..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .as("classes residing in '..domain..' or '..application..' must not depend on '..adapter..'")
                .because("adapters depend on ports and core code, never the reverse (ADR-003)");
    }

    /**
     * Rule D &mdash; cross-module persistence isolation: no class under {@code basePackage}
     * may depend on another top-level module's {@code adapter.out.persistence} classes.
     *
     * <p>Parameterized by {@code basePackage} so the identical rule logic can be applied both
     * to the real module tree ({@code com.systemdesignlab.platform}) and to a synthetic
     * fixture tree used to prove the rule detects a violation.
     */
    public static ArchRule crossModulePersistenceIsolation(String basePackage) {
        // Deliberately classes() rather than noClasses(): noClasses().should(condition) negates
        // a hand-written ArchCondition's satisfied/violated semantics, which only composes
        // correctly with the DSL's own built-in conditions (e.g. dependOnClassesThat()). With a
        // fully custom ArchCondition we want our own "violated" events to mean exactly that, so
        // the rule reads as its natural positive form instead: "classes should only depend on
        // their own module's persistence adapter".
        Pattern modulePattern = Pattern.compile(Pattern.quote(basePackage) + "\\.([^.]+)(\\..*)?");
        return classes()
                .should(onlyDependOnOwnModulePersistence(modulePattern))
                .as("classes under '" + basePackage
                        + "' must only depend on their own module's '..adapter.out.persistence..'")
                .because("cross-module collaboration must go through published application ports, "
                        + "not another module's persistence implementation (ADR-002, ADR-018)");
    }

    /**
     * Rule E &mdash; no cyclic dependencies between the top-level modules directly under
     * {@code basePackage}.
     */
    public static ArchRule noCyclicModuleDependencies(String basePackage) {
        return SlicesRuleDefinition.slices()
                .matching(basePackage + ".(*)..")
                .namingSlices("module '$1'")
                .should().beFreeOfCycles();
    }

    /**
     * Rule F &mdash; controller boundary: inbound REST adapters must not depend on outbound
     * persistence adapters directly; they must go through application input ports/use cases.
     * Written against the generic {@code ..adapter.in.rest..} / {@code ..adapter.out.persistence..}
     * package patterns so it stays useful once real controllers are added in later work
     * packages.
     */
    public static ArchRule controllerBoundary() {
        return noClasses()
                .that().resideInAPackage("..adapter.in.rest..")
                .should().dependOnClassesThat().resideInAPackage("..adapter.out.persistence..")
                .as("classes residing in '..adapter.in.rest..' must not depend on '..adapter.out.persistence..'")
                .because("REST controllers must interact with application input ports/use cases, not "
                        + "repositories, directly");
    }

    private static ArchCondition<JavaClass> onlyDependOnOwnModulePersistence(Pattern modulePattern) {
        return new ArchCondition<>("only depend on its own module's persistence adapters") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                String sourceModule = moduleOf(javaClass.getPackageName(), modulePattern);
                if (sourceModule == null) {
                    return;
                }
                for (Dependency dependency : javaClass.getDirectDependenciesFromSelf()) {
                    JavaClass target = dependency.getTargetClass();
                    String targetModule = moduleOf(target.getPackageName(), modulePattern);
                    boolean crossesIntoForeignPersistence = targetModule != null
                            && !targetModule.equals(sourceModule)
                            && target.getPackageName().contains(".adapter.out.persistence");
                    if (crossesIntoForeignPersistence) {
                        events.add(SimpleConditionEvent.violated(dependency,
                                dependency.getDescription() + " -- module '" + sourceModule
                                        + "' must not depend on module '" + targetModule
                                        + "'s persistence adapter"));
                    }
                }
            }
        };
    }

    private static String moduleOf(String packageName, Pattern modulePattern) {
        Matcher matcher = modulePattern.matcher(packageName);
        return matcher.matches() ? matcher.group(1) : null;
    }
}

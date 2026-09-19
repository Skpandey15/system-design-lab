package com.systemdesignlab.platform.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

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
     * Rule F &mdash; controller boundary: an inbound REST adapter must not bypass the module's
     * input-port boundary. It may only depend on {@code ..port.in..} (its declared use-case
     * contract), its own package, and approved framework/JDK/shared technical dependencies
     * &mdash; never on an application service, domain model, or any outbound adapter directly.
     *
     * <p>Expressed as a positive allowlist ({@code onlyDependOnClassesThat}) rather than a
     * growing blacklist of forbidden targets: a blacklist that only names
     * {@code ..adapter.out.persistence..} (as an earlier version of this rule did) misses
     * {@code ..application..}, {@code ..domain..}, and other {@code ..adapter.out..} adapters
     * (for example a messaging adapter) entirely. The allowlist enforces the actual boundary
     * &mdash; "may only reach application code through {@code port.in}" &mdash; directly, and
     * stays useful once real controllers are added in later work packages.
     */
    public static ArchRule controllerBoundary() {
        DescribedPredicate<JavaClass> allowedForInboundRestAdapters = resideInAnyPackage(
                "..adapter.in.rest..",
                "..port.in..",
                "java..",
                "javax..",
                "jakarta..",
                "org.springframework..",
                "com.systemdesignlab.platform.shared..")
                .as("its own REST adapter package, an input port, or an approved "
                        + "framework/JDK/shared technical package");

        return classes()
                .that().resideInAPackage("..adapter.in.rest..")
                .should().onlyDependOnClassesThat(allowedForInboundRestAdapters)
                .as("classes residing in '..adapter.in.rest..' must only depend on '..port.in..' "
                        + "(plus their own package and approved framework/JDK/shared technical "
                        + "dependencies)")
                .because("an inbound REST adapter must invoke application functionality through the "
                        + "module's input-port/use-case contract, not by bypassing it to reach "
                        + "application, domain, or outbound adapter implementations directly "
                        + "(ADR-003 Hexagonal Architecture)");
    }

    /**
     * Rule G &mdash; shared-kernel framework independence: classes residing in the shared kernel
     * ({@code ..shared..}) must not depend on Spring, JPA, or any adapter, exactly like the
     * domain layer (Rule A). WP-03 primitives (Money, DomainId, CorrelationId, DomainException)
     * must stay framework- and infrastructure-independent so every bounded context can safely
     * depend on them.
     */
    public static ArchRule sharedKernelFrameworkIndependence() {
        return noClasses()
                .that().resideInAPackage("..shared..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "..adapter..")
                .as("classes residing in '..shared..' must not depend on Spring, JPA, or adapters")
                .because("shared domain primitives must remain framework- and infrastructure-"
                        + "independent so every bounded context can safely depend on them (WP-03)");
    }

    /**
     * Rule H &mdash; shared-kernel module independence: no class under {@code basePackage}'s
     * {@code shared} package may depend on any sibling top-level module package. Mirrors Rule D's
     * parameterized, regex-based module detection so it can be proven against a synthetic fixture
     * tree, the same way Rule D is.
     */
    public static ArchRule sharedKernelModuleIndependence(String basePackage) {
        Pattern modulePattern = Pattern.compile(Pattern.quote(basePackage) + "\\.([^.]+)(\\..*)?");
        return classes()
                .that().resideInAPackage(basePackage + ".shared..")
                .should(onlyDependOnNonBusinessModules(modulePattern))
                .as("classes under '" + basePackage + ".shared..' must not depend on any sibling "
                        + "business module")
                .because("shared domain primitives must not depend back on business modules, or "
                        + "the shared kernel becomes coupled to one bounded context (WP-03)");
    }

    /**
     * Rule I &mdash; no floating-point domain monetary representation: no field in the domain,
     * application, or shared-kernel layers may be declared {@code double}/{@code float}/
     * {@code Double}/{@code Float} (ADR-014: "Do not use float/double for authoritative money.").
     * Scoped to those layers (not adapters) to protect authoritative domain state specifically,
     * avoiding false positives in unrelated infrastructure code.
     */
    public static ArchRule noFloatingPointDomainMonetaryRepresentation() {
        return noFields()
                .that().areDeclaredInClassesThat().resideInAnyPackage("..domain..", "..application..", "..shared..")
                .should().haveRawType(double.class)
                .orShould().haveRawType(float.class)
                .orShould().haveRawType(Double.class)
                .orShould().haveRawType(Float.class)
                .as("no field in '..domain..', '..application..' or '..shared..' may be of type "
                        + "double/float/Double/Float")
                .because("float/double are never authoritative monetary representations (ADR-014); "
                        + "Money.amount() is BigDecimal");
    }

    private static ArchCondition<JavaClass> onlyDependOnNonBusinessModules(Pattern modulePattern) {
        return new ArchCondition<>("only depend on non-business-module classes") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (Dependency dependency : javaClass.getDirectDependenciesFromSelf()) {
                    JavaClass target = dependency.getTargetClass();
                    String targetModule = moduleOf(target.getPackageName(), modulePattern);
                    boolean dependsOnBusinessModule = targetModule != null && !targetModule.equals("shared");
                    if (dependsOnBusinessModule) {
                        events.add(SimpleConditionEvent.violated(dependency,
                                dependency.getDescription() + " -- shared kernel must not depend on "
                                        + "module '" + targetModule + "'"));
                    }
                }
            }
        };
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

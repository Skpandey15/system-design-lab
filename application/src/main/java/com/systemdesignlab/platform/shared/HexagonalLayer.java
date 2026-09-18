package com.systemdesignlab.platform.shared;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares which Hexagonal Architecture layer a bounded-context package belongs to (for
 * example {@code "domain"}, {@code "application"}, {@code "port.in"}, {@code "port.out"},
 * {@code "adapter.in.rest"}, {@code "adapter.out.persistence"}).
 *
 * <p>Applied to each layer's {@code package-info.java}. This is deliberately the only
 * artifact placed in a layer package at WP-01: a plain, undocumented {@code package-info.java}
 * with no annotation compiles to no {@code .class} file at all, which would leave every layer
 * package invisible to the architecture fitness tests in
 * {@code com.systemdesignlab.platform.architecture} (test sources). This annotation is the
 * minimum real artifact needed to make those packages &mdash; and therefore the fitness
 * tests &mdash; actually exist and be checkable.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface HexagonalLayer {

    /** The layer name, e.g. {@code "domain"} or {@code "adapter.out.persistence"}. */
    String value();
}

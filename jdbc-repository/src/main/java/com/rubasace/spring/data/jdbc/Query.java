package com.rubasace.spring.data.jdbc;

import org.springframework.data.annotation.QueryAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
//TODO give use to this annotation to smooth code
@QueryAnnotation
public @interface Query {

    /**
     * Defines the JPA query to be executed when the annotated method is called.
     */
    String value() default "";

    /**
     * Defines a special count query that shall be used for pagination queries to lookup the total number of elements for
     * a page. If non is configured we will derive the count query from the method name.
     */
    String countQuery() default "";

    /**
     * Defines the projection part of the count query that is generated for pagination. If neither {@link #countQuery()}
     * not {@link #countProjection()} is configured we will derive the count query from the method name.
     *
     * @return
     * @since 1.6
     */
    String countProjection() default "";

    /**
     * Configures whether the given query is a native one. Defaults to {@literal false}.
     */
    boolean nativeQuery() default false;

    /**
     * The named query to be used. If not defined, a {@link javax.persistence.NamedQuery} with name of
     * {@code $ domainClass}.${queryMethodName}} will be used.
     */
    String name() default "";

    /**
     * Returns the name of the {@link javax.persistence.NamedQuery} to be used to execute count queries when pagination is
     * used. Will default to the named query name configured suffixed by {@code .count}.
     *
     * @return
     * @see #name()
     */
    String countName() default "";
}

package xyz.hamster.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generate Repository for marked type
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GenerateReactiveRepository {
    /**
     * Entity ID type
     */
    EntityIdType id() default EntityIdType.INTEGER;

    /**
     * Add 'getAllByActive' method
     */
    boolean hasActiveFlag() default false;

    /**
     * Destination package of class. Default - the same package as entity class
     */
    String destinationPackage() default "";
}

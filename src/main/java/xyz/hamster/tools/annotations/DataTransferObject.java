package xyz.hamster.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generate Data Transfer Object for marked entity
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DataTransferObject {
    /**
     * Generate builder
     */
    boolean builder() default false;

    /**
     * Generate default constructor
     */
    boolean constructor() default false;

    /**
     * Generate all args constructor
     */
    boolean allArgsConstructor() default false;

    /**
     * Destination package of class. Default - the same package as entity class
     */
    String destinationPackage() default "";

    /**
     * Destination class name of DTO object. Default - original name + 'Dto'
     */
    String className() default "";
}

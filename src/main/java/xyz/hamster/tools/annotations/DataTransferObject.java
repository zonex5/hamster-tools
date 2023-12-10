package xyz.hamster.tools.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generate Data Transfer Object for marked type
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

    String destinationPackage() default "";

    String nameSuffix() default "Dto";
}

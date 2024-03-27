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
     * Entity id type
     */
    EntityIdType id() default EntityIdType.INTEGER;

    boolean hasActiveFlag() default false;

    String destinationPackage() default "";
}

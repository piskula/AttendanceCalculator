package sk.oravcok.posta.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 *  Created by Ondrej Oravcok on 03-Nov-16.
 * inspired by Martin Styk https://github.com/MartinStyk/pa165-activity-tracker
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeSequenceValidator.class)
@Documented
public @interface TimeSequence {
    String message() default "Not correct order of time attributes, e.g. one should be before another";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] members() default {};
}

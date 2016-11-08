package sk.oravcok.posta.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalTime;

/**
 *  Created by Ondrej Oravcok on 03-Nov-16.
 * inspired by Martin Styk https://github.com/MartinStyk/pa165-activity-tracker
 */
public class TimeSequenceValidator implements ConstraintValidator<TimeSequence, Object> {

    private TimeSequence annotation;

    @Override
    public void initialize(TimeSequence constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object annotatedObject, ConstraintValidatorContext context) {
        String[] members = annotation.members();

        LocalTime previousTime = LocalTime.MIN;

        for (String member : members) {
            Field field;
            try {
                field = annotatedObject.getClass().getDeclaredField(member);
                field.setAccessible(true);

                LocalTime value = LocalTime.class.cast(field.get(annotatedObject));
                if (value.isBefore(previousTime)) {
                    return false;
                }
                previousTime = value;
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException("Error while reading member " + member + " on class " +
                        annotatedObject.getClass().getName());
            }

        }
        return true;
    }
}

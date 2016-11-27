package sk.oravcok.posta.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation, which cause translating downstream-layers exceptions
 *
 * Created by Ondrej Oravcok on 27-Nov-16.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceExceptionTranslate {
}

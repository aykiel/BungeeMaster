package me.tonymaster21.bungeemaster.spigot.skript.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andrew Tran
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Documentation {
    String name() default "";
    String description() default "";
    Example[] examples();
    String[] syntax();
}

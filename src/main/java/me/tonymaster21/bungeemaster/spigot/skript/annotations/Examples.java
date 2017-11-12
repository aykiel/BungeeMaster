package me.tonymaster21.bungeemaster.spigot.skript.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * @author Andrew Tran
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Examples {
    Example[] value();
}

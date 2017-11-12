package me.tonymaster21.bungeemaster.spigot.skript;

import ch.njol.skript.lang.util.SimpleExpression;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;

/**
 * @author Andrew Tran
 */
public abstract class BMExpession<T> extends SimpleExpression<T>{
    public BungeeMaster getBungeeMaster() {
        return BungeeMaster.getBungeeMaster();
    }
}

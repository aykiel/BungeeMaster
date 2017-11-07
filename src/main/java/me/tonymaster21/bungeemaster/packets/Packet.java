package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

public abstract class Packet<T> implements Serializable {

    private static final long serialVersionUID = 3155928254662028879L;
    private String name;
    private boolean returning;
    private Class<? extends T> returningClass;
    private T object;

    public Packet(String name, boolean returning, Class<? extends T> returningClass, T object) {
        this.name = name;
        this.returning = returning;
        this.returningClass = returningClass;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public boolean isReturning() {
        return returning;
    }

    public Class<? extends T> getReturningClass() {
        return returningClass;
    }

    public T getObject() {
        return object;
    }
}

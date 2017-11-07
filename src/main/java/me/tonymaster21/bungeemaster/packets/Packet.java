package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

public abstract class Packet<T> implements Serializable {

    private static final long serialVersionUID = 3155928254662028879L;
    private String name;
    private PacketDirection packetDirection;
    private boolean returning;
    private Class<? extends T> returningClass;
    private T object;
    private char[] password;

    public Packet(String name, PacketDirection packetDirection, boolean returning, Class<? extends T> returningClass) {
        this.name = name;
        this.packetDirection = packetDirection;
        this.returning = returning;
        this.returningClass = returningClass;
    }

    public Packet(String name, PacketDirection packetDirection, boolean returning, Class<? extends T> returningClass, T object) {
        this.name = name;
        this.packetDirection = packetDirection;
        this.returning = returning;
        this.returningClass = returningClass;
        this.object = object;
    }

    public Packet(String name, PacketDirection packetDirection, boolean returning, Class<? extends T> returningClass, T object, char[] password) {
        this.name = name;
        this.packetDirection = packetDirection;
        this.returning = returning;
        this.returningClass = returningClass;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public PacketDirection getPacketDirection() {
        return packetDirection;
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

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}

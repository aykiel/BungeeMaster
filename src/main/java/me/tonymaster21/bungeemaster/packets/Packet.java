package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

public abstract class Packet<R> implements Serializable {
    private static final long serialVersionUID = 3155928254662028879L;
    private String name;
    private PacketDirection packetDirection;
    private boolean returning;
    private Class<? extends R> returningClass;
    private char[] password;

    public Packet(String name, PacketDirection packetDirection, boolean returning, Class<? extends R> returningClass) {
        this.name = name;
        this.packetDirection = packetDirection;
        this.returning = returning;
        this.returningClass = returningClass;
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

    public Class<? extends R> getReturningClass() {
        return returningClass;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}

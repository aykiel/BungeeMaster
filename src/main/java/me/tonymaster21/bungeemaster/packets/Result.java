package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

/**
 * @author Andrew Tran
 */
public class Result implements Serializable{
    private static final long serialVersionUID = -5831760383136418733L;
    private Object object;
    private PacketStatus packetStatus;

    public Result(Object object, PacketStatus packetStatus) {
        this.object = object;
        this.packetStatus = packetStatus;
    }

    public Object getObject() {
        return object;
    }

    public PacketStatus getPacketStatus() {
        return packetStatus;
    }
}

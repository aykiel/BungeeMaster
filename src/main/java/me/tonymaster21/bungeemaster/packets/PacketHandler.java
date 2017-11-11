package me.tonymaster21.bungeemaster.packets;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public abstract class PacketHandler<T extends Packet> {
    private Class<T> packetClass;

    public PacketHandler(Class<T> packetClass) {
        this.packetClass = packetClass;
    }

    public Class<T> getPacketClass() {
        return packetClass;
    }

    public abstract Result handlePacket(T packet, Socket socket);

    public Result getSuccessfulResult(Object object){
        return new Result(object, PacketStatus.SUCCESSFUL);
    }
}

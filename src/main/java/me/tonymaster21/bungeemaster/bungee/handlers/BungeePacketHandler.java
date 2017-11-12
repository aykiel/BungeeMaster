package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketHandler;

/**
 * @author Andrew Tran
 */
public abstract class BungeePacketHandler<T extends Packet> extends PacketHandler<T>{
    protected BungeeMaster bungeeMaster;

    public BungeePacketHandler(Class<T> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass);
        this.bungeeMaster = bungeeMaster;
    }

    public BungeeMaster getBungeeMaster() {
        return bungeeMaster;
    }

    public void setBungeeMaster(BungeeMaster bungeeMaster) {
        this.bungeeMaster = bungeeMaster;
    }
}

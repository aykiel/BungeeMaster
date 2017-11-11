package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.bungee.BungeePacketHandler;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.HeartbeatPacket;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class HeartbeatPacketHandler extends BungeePacketHandler<HeartbeatPacket>{
    public HeartbeatPacketHandler(Class<HeartbeatPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(HeartbeatPacket packet, Socket socket) {
        return getSuccessfulResult(System.currentTimeMillis());
    }
}

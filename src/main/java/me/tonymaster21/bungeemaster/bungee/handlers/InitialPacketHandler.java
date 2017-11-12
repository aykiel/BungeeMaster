package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.spigot.InitialPacket;
import me.tonymaster21.bungeemaster.packets.Result;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class InitialPacketHandler extends BungeePacketHandler<InitialPacket>{
    public InitialPacketHandler(Class<InitialPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(InitialPacket packet, Socket socket) {
        return getSuccessfulResult(true);
    }
}

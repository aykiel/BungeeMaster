package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.BroadcastMessagePacket;

import java.net.Socket;
import java.util.Arrays;

/**
 * @author Andrew Tran
 */
public class BroadcastMessagePacketHandler extends BungeeActionPacketHandler<BroadcastMessagePacket> {
    public BroadcastMessagePacketHandler(Class<BroadcastMessagePacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(BroadcastMessagePacket packet, Socket socket) {
        String[] messages = packet.getMessages();
        if (messages == null) {
            return getErrorResult("Messages is null");
        }
        Arrays.stream(messages).forEach(bungeeMaster.getProxy()::broadcast);
        return getSuccessfulResult();
    }
}

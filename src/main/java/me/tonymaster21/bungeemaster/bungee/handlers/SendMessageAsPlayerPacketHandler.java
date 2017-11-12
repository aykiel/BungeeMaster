package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.SendMessageAsPlayerPacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class SendMessageAsPlayerPacketHandler extends BungeeActionPacketHandler<SendMessageAsPlayerPacket> {
    public SendMessageAsPlayerPacketHandler(Class<SendMessageAsPlayerPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(SendMessageAsPlayerPacket packet, Socket socket) {
        String sender = packet.getSender();
        String message = packet.getMessage();
        if (sender == null) {
            return getErrorResult("Sender is null");
        }
        if (message == null) {
            return getErrorResult("Message is null");
        }
        ProxiedPlayer proxiedPlayer = bungeeMaster.getPlayer(sender);
        if (proxiedPlayer == null) {
            return getErrorResult("Player "  + sender + " is not online");
        }
        proxiedPlayer.chat(message);
        return getSuccessfulResult();
    }
}

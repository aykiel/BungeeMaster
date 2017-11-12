package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.SendMessagePacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class SendMessagePacketHandler extends BungeeEffectPacketHandler<SendMessagePacket>{
    public SendMessagePacketHandler(Class<SendMessagePacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(SendMessagePacket packet, Socket socket) {
        String player = packet.getPlayer();
        if (player == null) {
            return getErrorResult("Player is null");
        }
        ProxiedPlayer proxiedPlayer = getBungeeMaster().getProxy().getPlayer(player);
        if (proxiedPlayer == null) {
            return getErrorResult("Player " + player + " is not online");
        }
        String[] messages = packet.getMessages();
        if (messages == null) {
            return getErrorResult("Messages is null");
        }
        proxiedPlayer.sendMessages(messages);
        return getSuccessfulResult();
    }
}

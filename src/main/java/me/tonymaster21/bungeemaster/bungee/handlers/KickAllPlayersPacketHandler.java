package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.KickAllPlayersPacket;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class KickAllPlayersPacketHandler extends BungeeActionPacketHandler<KickAllPlayersPacket> {
    public KickAllPlayersPacketHandler(Class<KickAllPlayersPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(KickAllPlayersPacket packet, Socket socket) {
        bungeeMaster.getProxy().getPlayers().forEach(
                player -> player.disconnect(packet.getReason() == null ? "" : packet.getReason()));
        return getSuccessfulResult();
    }
}

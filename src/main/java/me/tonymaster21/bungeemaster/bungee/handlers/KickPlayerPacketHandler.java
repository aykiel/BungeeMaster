package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.KickPlayerPacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class KickPlayerPacketHandler extends BungeeEffectPacketHandler<KickPlayerPacket> {
    public KickPlayerPacketHandler(Class<KickPlayerPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(KickPlayerPacket packet, Socket socket) {
        String player = packet.getPlayer();
        if (player == null) {
            return getErrorResult("Player is null");
        }
        ProxiedPlayer proxiedPlayer = getBungeeMaster().getProxy().getPlayer(player);
        if (proxiedPlayer == null) {
            return getErrorResult("Player " + player + " is not online");
        }
        proxiedPlayer.disconnect(packet.getReason() == null ? "" : packet.getReason());
        return getSuccessfulResult();
    }
}

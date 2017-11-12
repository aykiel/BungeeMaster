package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.ConnectPlayerPacket;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class ConnectPlayerPacketHandler extends BungeeEffectPacketHandler<ConnectPlayerPacket>{
    public ConnectPlayerPacketHandler(Class<ConnectPlayerPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(ConnectPlayerPacket packet, Socket socket) {
        String player = packet.getPlayer();
        if (player == null) {
            return getErrorResult("Player is null");
        }
        ProxiedPlayer proxiedPlayer = getBungeeMaster().getProxy().getPlayer(player);
        if (proxiedPlayer == null) {
            return getErrorResult("Player " + player + " is not online");
        }
        String destination = packet.getDestination();
        if (destination == null) {
            return getErrorResult("Destination is null");
        }
        boolean remote = packet.isRemote();
        ServerInfo serverInfo;
        if (remote) {
            String[] split = destination.split(":");
            destination = split[0];
            int port = Integer.valueOf(split.length >= 2 ? split[1] : "25565");
            serverInfo = getBungeeMaster().getProxy().constructServerInfo(destination, InetSocketAddress.createUnresolved(destination, port), "", false);
        } else {
            serverInfo = getBungeeMaster().getProxy().getServerInfo(destination);
            if (serverInfo == null) {
                return getErrorResult("Invalid server name: " + destination);
            }
        }
        proxiedPlayer.connect(serverInfo);
        return getSuccessfulResult();
    }
}

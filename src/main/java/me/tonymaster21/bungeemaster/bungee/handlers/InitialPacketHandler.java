package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BMServer;
import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.InitialPacket;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
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
        int port = packet.getPort();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(socket.getInetAddress(), port);
        BMServer bmServer = new BMServer(packet.getUuid(), inetSocketAddress);
        ServerInfo serverInfo = null;
        for (ServerInfo currentServerInfo : getBungeeMaster().getProxy().getServers().values()){
            if (currentServerInfo.getAddress().getAddress().equals(inetSocketAddress.getAddress())
                    && currentServerInfo.getAddress().getPort() == packet.getMinecraftPort()) {
                serverInfo = currentServerInfo;
                break;
            }
        }
        getBungeeMaster().getLogger().info(
                (serverInfo == null ? "A BungeeMaster instance" : "\"" + serverInfo.getName() + "\"")
                + " has connected on " + inetSocketAddress.toString().substring(1));
        bmServer.setServerInfo(serverInfo);
        if (serverInfo != null) {
            getBungeeMaster().getServerUUIDMap().put(serverInfo.getName(), packet.getUuid());
        }
        getBungeeMaster().getServerMap().put(packet.getUuid(), bmServer);
        return getSuccessfulResult(true);
    }
}

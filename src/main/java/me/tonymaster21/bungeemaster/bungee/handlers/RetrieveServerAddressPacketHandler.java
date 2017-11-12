package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.RetrieveServerAddressPacket;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class RetrieveServerAddressPacketHandler extends BungeeActionPacketHandler<RetrieveServerAddressPacket>{
    public RetrieveServerAddressPacketHandler(Class<RetrieveServerAddressPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(RetrieveServerAddressPacket packet, Socket socket) {
        String name = packet.getServerName();
        if (name == null) {
            return getErrorResult("Server name is null");
        }
        ServerInfo serverInfo = getBungeeMaster().getProxy().getServerInfo(name);
        if (serverInfo == null) {
            return getErrorResult("Server " + name + " not found");
        }
        String address = serverInfo.getAddress().toString();
        if (address.contains("/")) {
            address = address.split("/")[1];
        }
        return getSuccessfulResult(address);
    }
}

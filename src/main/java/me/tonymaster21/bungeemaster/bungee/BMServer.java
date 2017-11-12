package me.tonymaster21.bungeemaster.bungee;

import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author Andrew Tran
 */
public class BMServer {
    private UUID uuid;
    private InetSocketAddress address;
    private ServerInfo serverInfo;

    public BMServer(UUID uuid, InetSocketAddress address) {
        this.uuid = uuid;
        this.address = address;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }
}

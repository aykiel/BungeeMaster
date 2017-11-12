package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.ActionPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class RetrieveServerAddressPacket extends ActionPacket{
    private static final long serialVersionUID = 8021962717204702070L;
    private String serverName;

    public RetrieveServerAddressPacket(String serverName) {
        super("RetrieveServerAddress", PacketDirection.SPIGOT_TO_BUNGEE);
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}

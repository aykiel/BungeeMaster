package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

import java.util.UUID;

/**
 * @author Andrew Tran
 */
public class InitialPacket extends Packet<Boolean> {
    private static final long serialVersionUID = -7361670123571564356L;
    private UUID uuid;
    private int port, minecraftPort;
    private boolean reconnection;

    public InitialPacket(UUID uuid, int port, int minecraftPort, boolean reconnection) {
        super("InitialPacket", PacketDirection.SPIGOT_TO_BUNGEE, true, Boolean.class);
        this.uuid = uuid;
        this.port = port;
        this.minecraftPort = minecraftPort;
        this.reconnection = reconnection;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMinecraftPort() {
        return minecraftPort;
    }

    public void setMinecraftPort(int minecraftPort) {
        this.minecraftPort = minecraftPort;
    }

    public boolean isReconnection() {
        return reconnection;
    }

    public void setReconnection(boolean reconnection) {
        this.reconnection = reconnection;
    }
}

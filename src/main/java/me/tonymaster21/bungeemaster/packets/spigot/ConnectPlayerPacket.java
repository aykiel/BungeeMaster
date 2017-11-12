package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.EffectPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class ConnectPlayerPacket extends EffectPacket {
    private static final long serialVersionUID = 4532292071398377016L;
    private String player;
    private String destination;
    private boolean remote = false;

    public ConnectPlayerPacket(String player, String destination, boolean remote) {
        super("ConnectPlayer", PacketDirection.SPIGOT_TO_BUNGEE);
        this.player = player;
        this.destination = destination;
        this.remote = remote;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
}

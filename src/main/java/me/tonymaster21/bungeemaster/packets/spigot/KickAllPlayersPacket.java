package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.EffectPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class KickAllPlayersPacket extends EffectPacket{
    private static final long serialVersionUID = -6419320529163644726L;
    private String reason;

    public KickAllPlayersPacket() {
        super("KickAllPlayers", PacketDirection.SPIGOT_TO_BUNGEE);
    }

    public KickAllPlayersPacket(String reason) {
        super("KickAllPlayers", PacketDirection.SPIGOT_TO_BUNGEE);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

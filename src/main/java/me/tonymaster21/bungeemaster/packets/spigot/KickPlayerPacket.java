package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.ActionPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class KickPlayerPacket extends ActionPacket {
    private static final long serialVersionUID = 5345165569327456292L;
    private String player, reason;

    public KickPlayerPacket(String player) {
        super("KickPlayer", PacketDirection.SPIGOT_TO_BUNGEE);
        this.player = player;
    }

    public KickPlayerPacket(String player, String reason) {
        super("KickPlayer", PacketDirection.SPIGOT_TO_BUNGEE);
        this.player = player;
        this.reason = reason;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.EffectPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class StopProxyPacket extends EffectPacket{
    private static final long serialVersionUID = -4094621525686852722L;
    private String reason;

    public StopProxyPacket() {
        super("StopProxy", PacketDirection.SPIGOT_TO_BUNGEE);
    }

    public StopProxyPacket(String reason) {
        super("StopProxy", PacketDirection.SPIGOT_TO_BUNGEE);
        this.reason = reason;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

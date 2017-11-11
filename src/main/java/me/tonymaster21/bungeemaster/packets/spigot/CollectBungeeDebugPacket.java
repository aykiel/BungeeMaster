package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class CollectBungeeDebugPacket extends Packet<BungeeDebugInfo> {
    private static final long serialVersionUID = -2840738802992458366L;

    public CollectBungeeDebugPacket() {
        super("CollectBungeeDebug", PacketDirection.SPIGOT_TO_BUNGEE, true, BungeeDebugInfo.class);
    }

}

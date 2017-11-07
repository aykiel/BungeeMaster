package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public class CollectBungeeDebugPacket extends Packet<BungeeDebugInfo>{
    private static final long serialVersionUID = -2840738802992458366L;

    public CollectBungeeDebugPacket() {
        super("CollectBungeeDebug", PacketDirection.SPIGOT_TO_BUNGEE, true, BungeeDebugInfo.class);
    }

}

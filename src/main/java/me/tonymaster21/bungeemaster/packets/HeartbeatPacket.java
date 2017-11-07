package me.tonymaster21.bungeemaster.packets;

public class HeartbeatPacket extends Packet<Long> {
    private static final long serialVersionUID = 8968762828630229612L;

    public HeartbeatPacket() {
        super("Heartbeat", PacketDirection.SPIGOT_TO_BUNGEE, true, Long.class);
    }
}

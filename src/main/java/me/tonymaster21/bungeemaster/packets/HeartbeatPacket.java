package me.tonymaster21.bungeemaster.packets;

public class HeartbeatPacket extends Packet<Long> {
    private long timestamp;

    public HeartbeatPacket(long timestamp) {
        super("Heartbeat", true, Long.class, timestamp);
        this.timestamp = timestamp;
    }

    public long getSentTimestamp() {
        return timestamp;
    }
}

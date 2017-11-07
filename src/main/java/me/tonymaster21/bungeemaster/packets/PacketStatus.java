package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public enum PacketStatus {
    INVALID_PASSWORD(false),
    INVALID_FORMAT(false),
    UNKNOWN_PACKET(false),
    SUCCESSFUL(true);
    boolean success;

    PacketStatus(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

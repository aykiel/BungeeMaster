package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public interface PacketHandler {
    Result handlePacket(Packet packet);
}

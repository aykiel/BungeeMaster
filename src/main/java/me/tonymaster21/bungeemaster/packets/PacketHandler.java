package me.tonymaster21.bungeemaster.packets;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public interface PacketHandler {
    Result handlePacket(Packet packet, Socket socket);
}

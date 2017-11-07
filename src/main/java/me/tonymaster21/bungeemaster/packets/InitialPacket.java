package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public class InitialPacket extends Packet<Boolean>{
    private static final long serialVersionUID = -7361670123571564356L;

    public InitialPacket() {
        super("InitialPacket", PacketDirection.SPIGOT_TO_BUNGEE, true, Boolean.class);
    }
}

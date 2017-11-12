package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public abstract class EffectPacket extends Packet<ActionResult>{
    public EffectPacket(String name, PacketDirection packetDirection) {
        super(name, packetDirection, true, ActionResult.class);
    }
}

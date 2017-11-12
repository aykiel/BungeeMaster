package me.tonymaster21.bungeemaster.packets;

/**
 * @author Andrew Tran
 */
public abstract class ActionPacket extends Packet<ActionResult>{
    public ActionPacket(String name, PacketDirection packetDirection) {
        super(name, packetDirection, true, ActionResult.class);
    }
}

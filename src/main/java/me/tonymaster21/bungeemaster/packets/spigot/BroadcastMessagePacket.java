package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.EffectPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class BroadcastMessagePacket extends EffectPacket{
    private static final long serialVersionUID = -7872598276448587699L;
    private String[] messages;

    public BroadcastMessagePacket(String[] messages) {
        super("BroadcastMessage", PacketDirection.SPIGOT_TO_BUNGEE);
        this.messages = messages;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }
}

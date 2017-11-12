package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.EffectPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class SendMessageAsPlayerPacket extends EffectPacket {
    private static final long serialVersionUID = -3471075400251750721L;

    private String sender, message;

    public SendMessageAsPlayerPacket(String sender, String message) {
        super("SendMessageAsPlayer", PacketDirection.SPIGOT_TO_BUNGEE);
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

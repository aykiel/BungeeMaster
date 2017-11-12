package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.ActionPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class SendMessagePacket extends ActionPacket {
    private static final long serialVersionUID = 1292212257951827016L;
    private String player;
    private String[] messages;

    public SendMessagePacket(String player, String[] messages) {
        super("SendMessage", PacketDirection.SPIGOT_TO_BUNGEE);
        this.player = player;
        this.messages = messages;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String[] getMessages() {
        return messages;
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }
}

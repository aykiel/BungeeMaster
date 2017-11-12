package me.tonymaster21.bungeemaster.packets.spigot;

import me.tonymaster21.bungeemaster.packets.ActionPacket;
import me.tonymaster21.bungeemaster.packets.PacketDirection;

/**
 * @author Andrew Tran
 */
public class ProxyCommandPacket extends ActionPacket {
    private static final long serialVersionUID = 4856437744128188069L;
    private String command;
    private boolean output;

    public ProxyCommandPacket(String command, boolean output) {
        super("ProxyCommand", PacketDirection.SPIGOT_TO_BUNGEE);
        this.command = command;
        this.output = output;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }
}

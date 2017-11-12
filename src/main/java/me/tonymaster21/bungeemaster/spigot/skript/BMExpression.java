package me.tonymaster21.bungeemaster.spigot.skript;

import ch.njol.skript.lang.util.SimpleExpression;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;

/**
 * @author Andrew Tran
 */
public abstract class BMExpression<T> extends SimpleExpression<T>{
    public BungeeMaster getBungeeMaster() {
        return BungeeMaster.getBungeeMaster();
    }

    public <R> R send(Packet<R> packet) {
        return getBungeeMaster().attemptSendPacket(packet);
    }
}

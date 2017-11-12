package me.tonymaster21.bungeemaster.spigot.skript;

import ch.njol.skript.lang.util.SimpleExpression;
import me.tonymaster21.bungeemaster.packets.ActionResult;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;

/**
 * @author Andrew Tran
 */
public abstract class BMExpression<T> extends SimpleExpression<T>{
    public BungeeMaster getBungeeMaster() {
        return BungeeMaster.getBungeeMaster();
    }

    public Object send(Packet<ActionResult> packet) {
        ActionResult actionResult = getBungeeMaster().attemptSendPacket(packet);
        if (!actionResult.isSuccess()){
            getBungeeMaster().getLogger().warning("Effect sending a " + packet.getName() + " packet failed. " +
                    (actionResult.getError() == null ? "" : "Error: " + actionResult.getError()));
            if (actionResult.getThrowable() != null){
                actionResult.getThrowable().printStackTrace();
            }
        }
        return actionResult.getObject();
    }
}

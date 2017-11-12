package me.tonymaster21.bungeemaster.spigot.skript;

import ch.njol.skript.lang.Effect;
import me.tonymaster21.bungeemaster.packets.EffectResult;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;

/**
 * @author Andrew Tran
 */
public abstract class BMEffect extends Effect{
    public BungeeMaster getBungeeMaster() {
        return BungeeMaster.getBungeeMaster();
    }

    public void send(Packet<EffectResult> packet) {
        EffectResult effectResult = getBungeeMaster().attemptSendPacket(packet);
        if (!effectResult.isSuccess()){
            getBungeeMaster().getLogger().warning("Effect sending a " + packet.getName() + " packet failed. " +
                    (effectResult.getError() == null ? "" : "Error: " + effectResult.getError()));
            if (effectResult.getThrowable() != null){
                effectResult.getThrowable().printStackTrace();
            }
        }
    }
}

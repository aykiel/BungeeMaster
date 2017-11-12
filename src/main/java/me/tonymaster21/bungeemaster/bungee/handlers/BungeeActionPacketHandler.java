package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.ActionResult;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketStatus;
import me.tonymaster21.bungeemaster.packets.Result;

/**
 * @author Andrew Tran
 */
public abstract class BungeeActionPacketHandler<T extends Packet<ActionResult>> extends BungeePacketHandler<T>{
    public BungeeActionPacketHandler(Class<T> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    public Result getSuccessfulResult() {
        return getSuccessfulResult(null);
    }

    @Override
    public Result getSuccessfulResult(Object object) {
        return new Result(new ActionResult(true, object), PacketStatus.SUCCESSFUL);
    }

    public Result getErrorResult(String error) {
        return new Result(new ActionResult(false, error), PacketStatus.SUCCESSFUL);
    }


}

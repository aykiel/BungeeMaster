package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.EffectResult;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketStatus;
import me.tonymaster21.bungeemaster.packets.Result;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public abstract class BungeeEffectPacketHandler<T extends Packet<EffectResult>> extends BungeePacketHandler<T>{
    public BungeeEffectPacketHandler(Class<T> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    public Result getSuccessfulResult() {
        return getSuccessfulResult(null);
    }

    @Override
    public Result getSuccessfulResult(Object object) {
        return new Result(new EffectResult(true, object), PacketStatus.SUCCESSFUL);
    }

    public Result getErrorResult(String error) {
        return new Result(new EffectResult(false, error), PacketStatus.SUCCESSFUL);
    }


}

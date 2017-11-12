package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.StopProxyPacket;

import java.net.Socket;

/**
 * @author Andrew Tran
 */
public class StopProxyPacketHandler extends BungeeEffectPacketHandler<StopProxyPacket>{
    public StopProxyPacketHandler(Class packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }


    @Override
    public Result handlePacket(StopProxyPacket packet, Socket socket) {
        String reason = packet.getReason();
        if (reason != null) {
            getBungeeMaster().getProxy().stop(reason);
        } else {
            getBungeeMaster().getProxy().stop();
        }
        return getSuccessfulResult();
    }
}

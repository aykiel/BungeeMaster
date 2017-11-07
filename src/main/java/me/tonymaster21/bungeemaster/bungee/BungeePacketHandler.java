package me.tonymaster21.bungeemaster.bungee;

import me.tonymaster21.bungeemaster.packets.*;

import java.util.Arrays;

/**
 * @author Andrew Tran
 */
public class BungeePacketHandler implements PacketHandler{
    private BungeeMaster bungeeMaster;

    public BungeePacketHandler(BungeeMaster bungeeMaster) {
        this.bungeeMaster = bungeeMaster;
    }

    public Result getSuccessfulResult(Object object){
        return new Result(object, PacketStatus.SUCCESSFUL);
    }

    @Override
    public Result handlePacket(Packet packet) {
        if (!Arrays.equals(bungeeMaster.getPassword(), packet.getPassword())){
            return new Result(null, PacketStatus.INVALID_PASSWORD);
        }
        if (packet instanceof InitialPacket) {
            return getSuccessfulResult(true);
        } else if (packet instanceof HeartbeatPacket){
            return getSuccessfulResult(System.currentTimeMillis());
        }
        return null;
    }
}

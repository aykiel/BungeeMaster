package me.tonymaster21.bungeemaster.bungee;

import me.tonymaster21.bungeemaster.packets.*;
import me.tonymaster21.bungeemaster.util.PropertyUtil;

import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    public Result handlePacket(Packet packet, Socket socket) {
        if (!Arrays.equals(bungeeMaster.getPassword(), packet.getPassword())){
            return new Result(null, PacketStatus.INVALID_PASSWORD);
        }
        if (!PacketDirection.SPIGOT_TO_BUNGEE.equals(packet.getPacketDirection())){
            return new Result(null, PacketStatus.WRONG_DIRECTION);
        }
        if (packet instanceof InitialPacket) {
            bungeeMaster.getLogger().info("Received initial packet from " + socket.getInetAddress() + ":" + socket.getPort());
            return getSuccessfulResult(true);
        } else if (packet instanceof HeartbeatPacket){
            return getSuccessfulResult(System.currentTimeMillis());
        } else if (packet instanceof CollectBungeeDebugPacket) {
            return getSuccessfulResult(new BungeeDebugInfo(PropertyUtil.getOS(),
                    PropertyUtil.getJavaVersion(), bungeeMaster.getProxy().getVersion(),
                    bungeeMaster.getDescription().getVersion(),
                    bungeeMaster.getProxy().getPluginManager().getPlugins().stream().map(plugin -> {
                        String name = plugin.getDescription().getName();
                        String mainClass = plugin.getClass().getCanonicalName();
                        String version = plugin.getDescription().getVersion();
                        return String.format("%s[main:%s,version:%s]", name, mainClass, version);
                    }).collect(Collectors.joining(", ")), bungeeMaster.getPort(),
                    bungeeMaster.getProxy().getPlayers().size()));
        }
        return new Result(null, PacketStatus.UNKNOWN_PACKET);
    }
}

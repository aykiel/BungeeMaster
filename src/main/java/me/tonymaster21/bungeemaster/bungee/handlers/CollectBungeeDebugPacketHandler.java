package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.BungeeDebugInfo;
import me.tonymaster21.bungeemaster.packets.spigot.CollectBungeeDebugPacket;
import me.tonymaster21.bungeemaster.util.PropertyUtil;

import java.net.Socket;
import java.util.stream.Collectors;

/**
 * @author Andrew Tran
 */
public class CollectBungeeDebugPacketHandler extends BungeePacketHandler<CollectBungeeDebugPacket>{
    public CollectBungeeDebugPacketHandler(Class<CollectBungeeDebugPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(CollectBungeeDebugPacket packet, Socket socket) {
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
}

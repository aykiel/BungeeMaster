package me.tonymaster21.bungeemaster.spigot;

import me.tonymaster21.bungeemaster.packets.BungeeDebugInfo;
import me.tonymaster21.bungeemaster.packets.CollectBungeeDebugPacket;
import me.tonymaster21.bungeemaster.packets.InitialPacket;
import me.tonymaster21.bungeemaster.packets.PacketException;
import me.tonymaster21.bungeemaster.util.PropertyUtil;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Andrew Tran
 */
public class BungeeMasterCommand implements CommandExecutor{
    public static final String DUMP_URL = "https://api.tonymaster21.me/bungeemaster/dump.php";
    private static Method getOnlinePlayersMethod;
    static {
        try {
            getOnlinePlayersMethod = Bukkit.getServer().getClass().getDeclaredMethod("getOnlinePlayers");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private BungeeMaster bungeeMaster;

    BungeeMasterCommand(BungeeMaster bungeeMaster) {
        this.bungeeMaster = bungeeMaster;
    }

    public List<Player> getOnlinePlayers(){
        try {
            Object players = getOnlinePlayersMethod.invoke(Bukkit.getServer());
            if (players instanceof Player[]){
                return new ArrayList<>(Arrays.asList((Player[]) players));
            } else if (players instanceof List){
                return new ArrayList<>((List<Player>) players);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new String[]{
                ChatColor.GOLD + "BungeeMaster " + bungeeMaster.getDescription().getVersion(),
                ChatColor.GOLD + "Status: " + (bungeeMaster.isLocked() ? ChatColor.RED + "Not Connected" : ChatColor.GREEN + "Connected (ping: " + bungeeMaster.getPing() + "ms)"),
                ChatColor.GOLD + "Usage: " + ChatColor.YELLOW + "/" + label + " [reconnect|reload|dump]"
            });
            return true;
        }
        String argument = args[0];
        if (argument.equals("reconnect") || argument.equals("connect")) {
            try {
                Socket socket = bungeeMaster.connect();
                boolean success = bungeeMaster.sendPacket(new InitialPacket(), socket);
                if (!success) {
                    throw new PacketException("Did not receive true after sending initial packet to BungeeMaster on BungeeCord, connection issues?");
                }
                bungeeMaster.setLocked(false);
                sender.sendMessage(ChatColor.GREEN + "BungeeMaster successfully connected, addon should function as normal now.");
            } catch (IOException | PacketException e) {
                sender.sendMessage("Failed to connect to socket and test connection, check console for more details.");
                e.printStackTrace();
            }
        } else if (argument.equals("reload")) {
            bungeeMaster.loadConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded configuration successfully.");
        } else if (argument.equals("dump") || argument.equals("debug")) {
            Bukkit.getScheduler().runTaskAsynchronously(bungeeMaster, () -> {
                Map<String,String> arguments = new HashMap<>();
                arguments.put("generate_time", Long.toString(Instant.now().getEpochSecond()));
                // SPIGOT DUMP
                arguments.put("spigot_os", PropertyUtil.getOS());
                arguments.put("spigot_java_version", PropertyUtil.getJavaVersion());
                arguments.put("spigot_version", Bukkit.getServer().getBukkitVersion());
                Plugin skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
                if (skriptPlugin == null){
                    arguments.put("spigot_skript_version", "N/A");
                } else {
                    arguments.put("spigot_skript_version", skriptPlugin.getDescription().getVersion());
                }
                arguments.put("spigot_bm_version", bungeeMaster.getDescription().getVersion());
                arguments.put("spigot_bm_bungee_port", Integer.toString(bungeeMaster.getPort()));
                arguments.put("spigot_bm_locked", Boolean.toString(bungeeMaster.isLocked()));
                String plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(plugin -> {
                    String name = plugin.getName();
                    String clazz = plugin.getClass().getCanonicalName();
                    String version = plugin.getDescription().getVersion();
                    return String.format("%s[main:%s,version:%s]", name, clazz, version);
                }).collect(Collectors.joining(", "));
                arguments.put("spigot_plugins", plugins);
                arguments.put("spigot_player_count", Integer.toString(getOnlinePlayers().size()));
                //BUNGEE DUMP
                arguments.put("bungee_success", "false");
                if (!bungeeMaster.isLocked()) {
                    BungeeDebugInfo bungeeDebugInfo = bungeeMaster.attemptSendPacket(new CollectBungeeDebugPacket());
                    if (bungeeDebugInfo != null) {
                        arguments.put("bungee_success", "true");
                        arguments.put("bungee_os", bungeeDebugInfo.getOs());
                        arguments.put("bungee_java_version", bungeeDebugInfo.getJavaVersion());
                        arguments.put("bungee_version", bungeeDebugInfo.getBungeeVersion());
                        arguments.put("bungee_bm_version", bungeeDebugInfo.getBungeeMasterVersion());
                        arguments.put("bungee_bm_port", Integer.toString(bungeeDebugInfo.getBungeeMasterPort()));
                        arguments.put("bungee_plugins", bungeeDebugInfo.getPlugins());
                        arguments.put("bungee_player_count", Integer.toString(bungeeDebugInfo.getPlayerCount()));
                    }
                }
                StringJoiner sj = new StringJoiner("&");
                Logger logger = bungeeMaster.getLogger();
                logger.info("== Debug Dump ==");
                try {
                    for (Map.Entry<String, String> entry : arguments.entrySet()) {
                        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                                + URLEncoder.encode(entry.getValue(), "UTF-8"));
                        logger.info(entry.getKey() + " = " + entry.getValue());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                logger.info("================");
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(DUMP_URL).openConnection();
                    connection.setFixedLengthStreamingMode(length);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(out);
                    outputStream.close();
                    InputStream inputStream = connection.getInputStream();
                    String result = IOUtils.toString(inputStream, "UTF-8");
                    inputStream.close();
                    if (connection.getResponseCode() != 200){
                        sender.sendMessage(ChatColor.RED + "HTTP Response Code: " + connection.getResponseCode());
                    }
                    sender.sendMessage(ChatColor.GREEN + "Debug Dump: " + ChatColor.YELLOW + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            return false;
        }
        return true;
    }
}

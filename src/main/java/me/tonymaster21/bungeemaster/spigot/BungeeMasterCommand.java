package me.tonymaster21.bungeemaster.spigot;

import me.tonymaster21.bungeemaster.packets.InitialPacket;
import me.tonymaster21.bungeemaster.packets.PacketException;
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
import java.util.*;
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
                ChatColor.GOLD + "Usage: " + ChatColor.YELLOW + "/" + label + "[reconnect|reload|dump]"
            });
            return true;
        }
        String argument = args[0];
        if (argument.equals("reconnect") || argument.equals("connect")) {
            if (!bungeeMaster.isLocked()) {
                sender.sendMessage(ChatColor.RED + "BungeeMaster is already connected");
                return true;
            }
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
            Map<String,String> arguments = new HashMap<>();
            arguments.put("os", System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
            arguments.put("java-version", System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")");
            arguments.put("bukkit-version", Bukkit.getServer().getBukkitVersion());
            Plugin skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
            if (skriptPlugin == null){
                arguments.put("skript-version", "N/A");
            } else {
                arguments.put("skript-version", skriptPlugin.getDescription().getVersion());
            }
            arguments.put("bungeemaster-version", bungeeMaster.getDescription().getVersion());
            arguments.put("locked", Boolean.toString(bungeeMaster.isLocked()));
            String plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(plugin -> {
                String name = plugin.getName();
                String clazz = plugin.getClass().getCanonicalName();
                String version = plugin.getDescription().getVersion();
                return String.format("%s[main:%s,version:%s]", name, clazz, version);
            }).collect(Collectors.joining(", "));
            arguments.put("plugins", plugins);
            arguments.put("player-count", Integer.toString(getOnlinePlayers().size()));
            StringJoiner sj = new StringJoiner("&");
            try {
                for (Map.Entry<String, String> entry : arguments.entrySet()) {
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                sender.sendMessage(ChatColor.GREEN + "Debug Dump: " + ChatColor.YELLOW + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return true;
    }
}

package me.tonymaster21.bungeemaster.spigot;

import ch.njol.skript.Skript;
import me.tonymaster21.bungeemaster.packets.*;
import me.tonymaster21.bungeemaster.packets.spigot.HeartbeatPacket;
import me.tonymaster21.bungeemaster.packets.spigot.InitialPacket;
import org.apache.commons.io.IOUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.Socket;

public class BungeeMaster extends JavaPlugin {
    private Metrics metrics;
    private File configFile = new File(getDataFolder(), "config.yml");
    private Configuration configuration;
    private volatile BungeeMasterConfig bungeeMasterConfig;
    private volatile boolean heartbeatDone = true;
    private volatile long ping;
    private boolean locked = false;

    @Override
    public void onEnable() {
        Plugin skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
        if (skriptPlugin == null){
            stop("You need Skript to be able to run this addon. Download Skript at https://github.com/bensku/Skript/releases");
            return;
        }
        Skript.registerAddon(this);
        metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> skriptPlugin.getDescription() == null ? null : skriptPlugin.getDescription().getVersion()));
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        if (!configFile.exists()){
            try (FileWriter fileWriter = new FileWriter(configFile)){
                IOUtils.copy(getResource("spigot/config.yml"), fileWriter, "UTF-8");
            } catch (IOException e) {
                getLogger().warning("Failed to copy default configuration");
                e.printStackTrace();
            }
        }
        loadConfig();
        try {
            Socket socket = connect();
            boolean status = sendPacket(new InitialPacket(), socket);
            if (status) {
                getLogger().info("Successfully connected to BungeeMaster on BungeeCord");
            } else {
                getLogger().warning("Did not receive true after sending initial packet to BungeeMaster on BungeeCord, connection issues?");
                lock();
            }
            try {
                socket.close();
            } catch (IOException e) {
                getLogger().warning("Failed to close initial socket, but that should be no problem");
                e.printStackTrace();
            }
        } catch (IOException | PacketException e) {
            getLogger().warning("Failed to connect to BungeeMaster on BungeeCord at " + getCombinedHost());
            e.printStackTrace();
            lock();
        }
        getCommand("bungeemaster").setExecutor(new BungeeMasterCommand(this));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (locked || !heartbeatDone) {
                return;
            }
            heartbeatDone = false;
            long timestamp = System.currentTimeMillis();
            Long remoteTimestamp = attemptSendPacket(new HeartbeatPacket());
            if (remoteTimestamp == null){
                return;
            }
            ping = remoteTimestamp - timestamp;
            heartbeatDone = true;
        }, 0, bungeeMasterConfig.getHeartbeatSeconds() * 20);
    }

    public void stop(String message) {
        getLogger().warning(message);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void loadConfig() {
        configuration = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection bungeeSection;
        if (configuration.contains("bungee")){
            bungeeSection = configuration.getConfigurationSection("bungee");
        } else {
            getLogger().warning("The section 'bungee' in the configuration is not found, " +
                    "defaults will be assumed. Delete the config file and restart to have a " +
                    "clean valid configuration file.");
            bungeeSection = configuration.createSection("bungee");
        }
        String host = bungeeSection.getString("host");
        int port = bungeeSection.getInt("port");
        char[] password = bungeeSection.getString("password", "").toCharArray();
        int heartbeatSeconds = bungeeSection.getInt("heartbeat-seconds", 30);
        int reconnectAttempts = bungeeSection.getInt("reconnect-attempts", 7);
        bungeeMasterConfig = new BungeeMasterConfig(host, port, password, heartbeatSeconds, reconnectAttempts);
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public File getConfigFile() {
        return configFile;
    }

    public BungeeMasterConfig getBungeeMasterConfig() {
        return bungeeMasterConfig;
    }

    public String getCombinedHost() {
        return String.format("%s:%d", bungeeMasterConfig.getHost(), bungeeMasterConfig.getPort());
    }

    public Socket connect() throws IOException {
        return new Socket(bungeeMasterConfig.getHost(), bungeeMasterConfig.getPort());
    }

    public <R> R attemptSendPacket(Packet<R> packet) {
        try {
            return sendPacket(packet, connect());
        } catch (IOException | PacketException e) {
            getLogger().warning("Failed to send packet, attempting reconnect and trying again");
            e.printStackTrace();
            Socket socket = attemptReconnect();
            if (socket != null){
                getLogger().info("Reconnect successful, attempting to send packet again");
                try {
                    return sendPacket(packet, connect());
                } catch (IOException | PacketException e1) {
                    getLogger().warning("Second attempt of sending packet failed again");
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    public <R> R sendPacket(Packet<R> packet, Socket socket) throws PacketException {
        if (bungeeMasterConfig.getPassword().length != 0){
            packet.setPassword(bungeeMasterConfig.getPassword());
        }
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(packet);
        } catch (IOException e) {
            throw new PacketException("Failed to write packet", e);
        }
        ObjectInputStream objectInputStream;
        Object object;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            object = objectInputStream.readObject();
        } catch (IOException |ClassNotFoundException e) {
            throw new PacketException("Failed to read result of packet", e);
        }
        if (object == null) {
            throw new PacketException("Result is null");
        }
        Result result = (Result) object;
        PacketStatus packetStatus = result.getPacketStatus();
        if (packetStatus == null || !packetStatus.isSuccess()){
            throw new PacketException("Packet result status is " + packetStatus);
        }
        if (packet.isReturning()){
            Object resultObject = result.getObject();
            if (!packet.getReturningClass().isAssignableFrom(resultObject.getClass())){
                throw new PacketException(String.format("Returning object from packet of type %s is not of type %s",
                        packet.getName(), packet.getReturningClass().getCanonicalName()));
            }
            return (R) resultObject;
        }
        return null;
    }

    public Socket attemptReconnect() {
        getLogger().warning(String.format("Attempting to reconnect with %d attempts", bungeeMasterConfig.getReconnectAttempts()));
        int attempts = 0;
        Throwable latestThrowable = null;
        while (attempts <= bungeeMasterConfig.getReconnectAttempts()) {
            try {
                return connect();
            } catch (IOException e1) {
                latestThrowable = e1;
            }
            attempts++;
        }
        getLogger().warning(String.format("Failed to reconnect to BungeeMaster on BungeeCord after %d attempts", bungeeMasterConfig.getReconnectAttempts()));
        if (latestThrowable != null){
            latestThrowable.printStackTrace();

        }
        lock();
        return null;
    }

    public void lock() {
        locked = true;
        getLogger().warning("Plugin will not work properly until '/bungeemaster reconnect' is ran. To reload the configuration run '/bungeemaster reload'.");
    }

    public long getPing() {
        return ping;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}

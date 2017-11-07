package me.tonymaster21.bungeemaster.spigot;

import me.tonymaster21.bungeemaster.packets.HeartbeatPacket;
import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketException;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.Socket;
import java.time.Instant;


public class BungeeMaster extends JavaPlugin {
    private File configFile = new File(getDataFolder(), "config.yml");
    private Configuration configuration;
    private String host;
    private int port;
    private char[] password;
    private int heartbeatSeconds;
    private int reconnectAttempts;
    private Socket socketClient;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private volatile long ping;

    @Override
    public void onEnable() {
        if (!getDataFolder().mkdirs()){
            getLogger().warning("Failed to make data folder");
        }
        try (FileWriter fileWriter = new FileWriter(configFile)){
            IOUtils.copy(getResource("spigot/config.yml"), fileWriter, "UTF-8");
        } catch (IOException e) {
            getLogger().warning("Failed to copy default configuration");
            e.printStackTrace();
        }
        configuration = YamlConfiguration.loadConfiguration(configFile);
        if (!configuration.contains("bungee")){
            stop("Configuration section 'bungee' not found in configuration");
            return;
        }
        ConfigurationSection bungeeSection = configuration.getConfigurationSection("bungee");
        host = bungeeSection.getString("host");
        port = bungeeSection.getInt("port");
        password = bungeeSection.getString("password", "").toCharArray();
        heartbeatSeconds = bungeeSection.getInt("heartbeat-seconds", 30);
        reconnectAttempts = bungeeSection.getInt("reconnect-attempts", 7);
        try {
            socketClient = new Socket(host, port);
            getLogger().info("Successfully connected to BungeeMaster on BungeeCord");
            objectOutputStream = new ObjectOutputStream(socketClient.getOutputStream());
            objectInputStream = new ObjectInputStream(socketClient.getInputStream());
        } catch (IOException e) {
            stop("Failed to connect to socketClient at " + getCombinedHost());
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (socketClient.isClosed()){
                attemptReconnect();
            }
            long timestamp = getTimestamp();
            ping = attemptSendPacket(new HeartbeatPacket(timestamp)) - timestamp;
        }, 0, heartbeatSeconds * 20);
    }

    @Override
    public void onDisable() {
        try {
            if (socketClient != null){
                socketClient.close();
            }
            if (objectOutputStream != null){
                objectOutputStream.close();
            }
            if (objectInputStream != null){
                objectInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(String message) {
        getLogger().warning(message);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public File getConfigFile() {
        return configFile;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getCombinedHost() {
        return String.format("%s:%d", host, port);
    }

    public char[] getPassword() {
        return password;
    }

    public int getHeartbeatSeconds() {
        return heartbeatSeconds;
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    public Socket getSocketClient() {
        return socketClient;
    }

    public <T> T attemptSendPacket(Packet<T> packet){
        try {
            return sendPacket(packet);
        } catch (PacketException e){
            getLogger().warning(String.format("Failed to send a %s packet", packet.getName()));
            e.printStackTrace();
            if (!attemptReconnect()){
                stop("Could not reconnect to BungeeMaster on BungeeCord");
            }
        }
        return null;
    }

    public <T> T sendPacket(Packet<T> packet) throws PacketException {
        try {
            objectOutputStream.writeObject(packet);
        } catch (IOException e) {
            throw new PacketException("Failed to write packet", e);
        }
        if (packet.isReturning()){
            Object object = null;
            try {
                object = objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new PacketException("Failed to read returning object from packet", e);
            }
            if (!packet.getReturningClass().isAssignableFrom(object.getClass())){
                throw new PacketException(String.format("Returning object from packet of type %s is not of type %s",
                        packet.getName(), packet.getReturningClass().getCanonicalName()));
            }
            return (T) object;
        }
        return null;
    }

    public long getTimestamp(){
        return Instant.now().getEpochSecond();
    }

    public boolean attemptReconnect() {
        getLogger().warning(String.format("Attempting to reconnect with %d attempts", reconnectAttempts));
        int attempts = 0;
        Throwable latestThrowable = null;
        boolean connected = false;
        while (attempts <= reconnectAttempts && !connected) {
            try {
                socketClient = new Socket(host, port);
                connected = true;
            } catch (IOException e1) {
                latestThrowable = e1;
            }
        }
        if (!connected) {
            getLogger().warning(String.format("Failed to reconnect to BungeeMaster on BungeeCord after %d attempts", reconnectAttempts));
            if (latestThrowable != null){
                latestThrowable.printStackTrace();
            }
        }
        return connected;
    }

    public long getPing() {
        return ping;
    }
}

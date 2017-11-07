package me.tonymaster21.bungeemaster.bungee;

import me.tonymaster21.bungeemaster.packets.Packet;
import me.tonymaster21.bungeemaster.packets.PacketHandler;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;

public class BungeeMaster extends Plugin {
    private Metrics metrics;
    private File configFile;
    private Configuration configuration;
    private String host;
    private InetAddress inetAddressHost;
    private int port;
    private char[] password;
    private PacketHandler packetHandler = new BungeePacketHandler(this);
    private ServerSocket serverSocket;

    @Override
    public void onEnable() {
        metrics = new Metrics(this);
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                Files.copy(getResourceAsStream("bungee/config.yml"), configFile.toPath());
            }
        } catch (IOException e) {
            getLogger().warning("Failed to copy default configuration");
            e.printStackTrace();
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            getLogger().warning("Failed to load configuration");
            e.printStackTrace();
        }
        host = configuration.getString("host", "0.0.0.0");
        port = configuration.getInt("port", 2112);
        password = configuration.getString("password", "").toCharArray();
        final Plugin plugin = this;
        try {
            inetAddressHost = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            getLogger().warning("Invalid host: " + host);
            e.printStackTrace();
        }
        try {
            if (inetAddressHost == null){
                inetAddressHost = InetAddress.getByName("0.0.0.0");
            }
            serverSocket = new ServerSocket(port, 25, inetAddressHost);
            getLogger().info("Successfully started BungeeMaster on BungeeCord on " + getCombinedHost());
            getProxy().getScheduler().runAsync(this, () -> {
                try {
                    while (!serverSocket.isClosed()) {
                        Socket socket = serverSocket.accept();
                        getProxy().getScheduler().runAsync(plugin, () -> {
                            try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                                Packet packet;
                                try {
                                    packet = (Packet) objectInputStream.readObject();
                                } catch (EOFException e) {
                                    return;
                                }
                                Object toReturn = packetHandler.handlePacket(packet);
                                objectOutputStream.writeObject(toReturn);
                            } catch (EOFException e) {
                                getLogger().warning("Socket did not send a packet with itself.");
                                e.printStackTrace();
                            } catch (IOException | ClassNotFoundException e) {
                                getLogger().info("Failed to handle packet");
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException e) {
                    getLogger().info("Failed to accept socket");
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}

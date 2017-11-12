package me.tonymaster21.bungeemaster.bungee;

import me.tonymaster21.bungeemaster.bungee.handlers.*;
import me.tonymaster21.bungeemaster.packets.*;
import me.tonymaster21.bungeemaster.packets.spigot.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BungeeMaster extends Plugin {
    private Metrics metrics;
    private File configFile;
    private Configuration configuration;
    private String host;
    private InetAddress inetAddressHost;
    private int port;
    private char[] password;
    private ServerSocket serverSocket;
    private List<PacketHandler<?>> packetHandlers = new ArrayList<>(Arrays.asList(
            new InitialPacketHandler(InitialPacket.class, this),
            new HeartbeatPacketHandler(HeartbeatPacket.class, this),
            new CollectBungeeDebugPacketHandler(CollectBungeeDebugPacket.class, this),
            new SendMessageAsPlayerPacketHandler(SendMessageAsPlayerPacket.class, this),
            new ConnectPlayerPacketHandler(ConnectPlayerPacket.class, this),
            new KickAllPlayersPacketHandler(KickAllPlayersPacket.class, this),
            new KickPlayerPacketHandler(KickPlayerPacket.class, this),
            new BroadcastMessagePacketHandler(BroadcastMessagePacket.class, this),
            new SendMessagePacketHandler(SendMessagePacket.class, this)
    ));

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
                                Result result;
                                if (getPassword().length != 0 && !Arrays.equals(getPassword(), packet.getPassword())){
                                    result = new Result(PacketStatus.INVALID_PASSWORD);
                                } else if (!PacketDirection.SPIGOT_TO_BUNGEE.equals(packet.getPacketDirection())
                                        && !PacketDirection.BIDIRECTIONAL.equals(packet.getPacketDirection())){
                                    result = new Result(PacketStatus.WRONG_DIRECTION);
                                } else {
                                    Optional<PacketHandler<?>> packetHandlerOptional = getPacketHandlers()
                                            .stream()
                                            .filter(packetHandler -> packetHandler.getPacketClass().isAssignableFrom(packet.getClass()))
                                            .findFirst();
                                    if (packetHandlerOptional.isPresent()){
                                        result = ((PacketHandler<Packet>) packetHandlerOptional.get()).handlePacket(packet, socket);
                                    } else {
                                        result = new Result(PacketStatus.UNKNOWN_PACKET);
                                        getLogger().warning("No packet handler found for packet class: " + packet.getClass().getCanonicalName());
                                    }
                                }
                                objectOutputStream.writeObject(result);
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

    public List<PacketHandler<?>> getPacketHandlers() {
        return packetHandlers;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}

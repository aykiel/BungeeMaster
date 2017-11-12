package me.tonymaster21.bungeemaster.bungee;

import me.tonymaster21.bungeemaster.bungee.handlers.*;
import me.tonymaster21.bungeemaster.packets.*;
import me.tonymaster21.bungeemaster.packets.spigot.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bstats.bungeecord.Metrics;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class BungeeMaster extends Plugin {
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-([0-9a-f]{4}-){3}[0-9a-f]{12}");
    private File configFile;
    private volatile BungeeMasterBungeeConfig config;
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
            new SendMessagePacketHandler(SendMessagePacket.class, this),
            new StopProxyPacketHandler(StopProxyPacket.class, this),
            new ProxyCommandPacketHandler(ProxyCommandPacket.class, this),
            new RetrieveServerAddressPacketHandler(RetrieveServerAddressPacket.class, this)
    ));

    private volatile Map<UUID,BMServer> serverMap = new HashMap<>();
    private volatile Map<String,UUID> serverUUIDMap = new HashMap<>();

    @Override
    public void onEnable() {
        new Metrics(this);
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        configFile = new File(getDataFolder(), "config.yml");
        loadConfig();
        getProxy().getPluginManager().registerCommand(this, new BungeeMasterBungeeCommand(this));
        final Plugin plugin = this;
        try {
            serverSocket = new ServerSocket(config.getPort(), 50, config.getInetAddressHost());
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
                                if (getConfig().getPassword().length != 0 && !Arrays.equals(getConfig().getPassword(), packet.getPassword())){
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
                    getLogger().warning("Failed to accept socket");
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

    public void loadConfig() {
        try {
            if (!configFile.exists()) {
                Files.copy(getResourceAsStream("bungee/config.yml"), configFile.toPath());
            }
        } catch (IOException e) {
            getLogger().warning("Failed to copy default configuration");
            e.printStackTrace();
        }
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            String host = configuration.getString("host", "0.0.0.0");
            int port = configuration.getInt("port", 2112);
            char[] password = configuration.getString("password", "").toCharArray();
            InetAddress inetAddressHost = null;
            try {
                inetAddressHost = InetAddress.getByName(host);
            } catch (UnknownHostException e) {
                getLogger().warning("Invalid host: " + host);
                e.printStackTrace();
            }
            if (inetAddressHost == null){
                inetAddressHost = InetAddress.getByName("0.0.0.0");
            }
            config = new BungeeMasterBungeeConfig(host, inetAddressHost, port, password);
        } catch (IOException e) {
            getLogger().warning("Failed to load configuration");
            e.printStackTrace();
        }
    }

    public BungeeMasterBungeeConfig getConfig() {
        return config;
    }

    public String getCombinedHost() {
        return String.format("%s:%d", getConfig().getHost(), getConfig().getPort());
    }

    public List<PacketHandler<?>> getPacketHandlers() {
        return packetHandlers;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ProxiedPlayer getPlayer(String representation) {
        if (UUID_PATTERN.matcher(representation).matches()) {
            UUID uuid = UUID.fromString(representation);
            ProxiedPlayer player = getProxy().getPlayer(uuid);
            if (player != null) {
                return player;
            }
        }
        return getProxy().getPlayer(representation);
    }

    public Map<UUID, BMServer> getServerMap() {
        return serverMap;
    }

    public Map<String, UUID> getServerUUIDMap() {
        return serverUUIDMap;
    }

    public BMServer getServerByUUID(UUID uuid) {
        return serverMap.get(uuid);
    }

    public BMServer getServerByName(String name) {
        return getServerByUUID(serverUUIDMap.get(name));
    }

    public BMServer getServerByServerInfo(ServerInfo serverInfo) {
        return getServerByName(serverInfo.getName());
    }
}

package me.tonymaster21.bungeemaster.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import me.tonymaster21.bungeemaster.packets.*;
import me.tonymaster21.bungeemaster.packets.spigot.HeartbeatPacket;
import me.tonymaster21.bungeemaster.packets.spigot.InitialPacket;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import org.apache.commons.io.IOUtils;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class BungeeMaster extends JavaPlugin{
    private static BungeeMaster bungeeMaster;

    private UUID uuid = UUID.randomUUID();
    private File configFile = new File(getDataFolder(), "config.yml");
    private volatile BungeeMasterSpigotConfig bungeeMasterSpigotConfig;
    private volatile boolean heartbeatDone = true;
    private volatile long ping;
    private boolean locked = false;
    private ServerSocket serverSocket;
    private int port;
    private int minecraftPort;

    private List<PacketHandler<?>> packetHandlers = new ArrayList<>(Arrays.asList(

    ));

    @Override
    public void onEnable() {
        bungeeMaster = this;
        Plugin skriptPlugin = Bukkit.getPluginManager().getPlugin("Skript");
        if (skriptPlugin == null){
            stop("You need Skript to be able to run this addon. Download Skript at https://github.com/bensku/Skript/releases");
            return;
        }
        SkriptAddon addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("me.tonymaster21.bungeemaster.spigot.skript", "elements");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.SimplePie("skript_version", () -> skriptPlugin.getDescription() == null ? null : skriptPlugin.getDescription().getVersion()));
        if (!getDataFolder().exists() && !getDataFolder().mkdir()){
            getLogger().info("Failed to create data folder");
        }
        minecraftPort = getServer().getPort();
        if (!configFile.exists()){
            try (FileWriter fileWriter = new FileWriter(configFile)){
                IOUtils.copy(getResource("spigot/config.yml"), fileWriter, "UTF-8");
            } catch (IOException e) {
                getLogger().warning("Failed to copy default configuration");
                e.printStackTrace();
            }
        }
        loadConfig();
        port = 3112;
        if (bungeeMasterSpigotConfig.isServerPortAuto()) {
            Throwable throwable = null;
            while (bungeeMasterSpigotConfig.isServerPortAuto() && port <= bungeeMasterSpigotConfig.getServerPort() + 1000) {
                try {
                    serverSocket = new ServerSocket(3112);
                    break;
                } catch (IOException e) {
                    throwable = e;
                    port++;
                }
            }
            if (serverSocket == null) {
                stop("Failed to start server socket automatically");
                if (throwable != null) {
                    throwable.printStackTrace();
                }
                return;
            }
        } else {
            port = bungeeMasterSpigotConfig.getServerPort();
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                stop("Failed to start server socket");
                e.printStackTrace();
                return;
            }
        }
        getLogger().info("Started server socket on port " + port);
        final JavaPlugin javaPlugin = this;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> {
                        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                            Packet packet;
                            try {
                                packet = (Packet) objectInputStream.readObject();
                            } catch (EOFException e) {
                                return;
                            }
                            Result result;
                            if (!Arrays.equals(packet.getPassword(), uuid.toString().toCharArray())) {
                                result = new Result(PacketStatus.INVALID_PASSWORD);
                            } else if (!PacketDirection.BUNGEE_TO_SPIGOT.equals(packet.getPacketDirection())
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
                } catch (IOException e) {
                    getLogger().warning("Failed to accept packet");
                    e.printStackTrace();
                }
            }
        });

        try {
            Socket socket = connect();
            boolean status = sendPacket(getInitialPacket(false), socket);
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
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (locked || !heartbeatDone) {
                return;
            }
            heartbeatDone = false;
            long timestamp = System.currentTimeMillis();
            Long remoteTimestamp = attemptSendPacket(new HeartbeatPacket());
            heartbeatDone = true;
            if (remoteTimestamp == null){
                return;
            }
            ping = remoteTimestamp - timestamp;
        }, 0, bungeeMasterSpigotConfig.getHeartbeatSeconds() * 20);

        getCommand("bungeemaster").setExecutor(new BungeeMasterSpigotCommand(this));
    }

    public static BungeeMaster getBungeeMaster() {
        return bungeeMaster;
    }

    public void stop(String message) {
        getLogger().warning(message);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void loadConfig() {
        Configuration configuration = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection bungeeSection;
        if (configuration.contains("bungee")){
            bungeeSection = configuration.getConfigurationSection("bungee");
        } else {
            getLogger().warning("The section 'bungee' in the configuration is not found, " +
                    "defaults will be assumed. Delete the config file and restart to have a " +
                    "clean valid configuration file.");
            bungeeSection = configuration.createSection("bungee");
        }
        boolean serverPortAuto = configuration.getBoolean("port-automatic", true);
        int serverPort = configuration.getInt("port", 3112);
        String host = bungeeSection.getString("host");
        int port = bungeeSection.getInt("port");
        char[] password = bungeeSection.getString("password", "").toCharArray();
        int heartbeatSeconds = bungeeSection.getInt("heartbeat-seconds", 30);
        int reconnectAttempts = bungeeSection.getInt("reconnect-attempts", 7);
        bungeeMasterSpigotConfig = new BungeeMasterSpigotConfig(serverPortAuto, serverPort, host, port, password, heartbeatSeconds, reconnectAttempts);
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPort() {
        return port;
    }

    public File getConfigFile() {
        return configFile;
    }

    public InitialPacket getInitialPacket(boolean reconnection) {
        return new InitialPacket(getUuid(), port, minecraftPort, reconnection);
    }

    public BungeeMasterSpigotConfig getBungeeMasterSpigotConfig() {
        return bungeeMasterSpigotConfig;
    }

    public String getCombinedHost() {
        return String.format("%s:%d", bungeeMasterSpigotConfig.getHost(), bungeeMasterSpigotConfig.getPort());
    }

    public Socket connect() throws IOException {
        return new Socket(bungeeMasterSpigotConfig.getHost(), bungeeMasterSpigotConfig.getPort());
    }

    public <R> R attemptSendPacket(Packet<R> packet) {
        try {
            return sendPacket(packet, connect());
        } catch (IOException e) {
            getLogger().warning("Failed to send packet, attempting reconnect and trying again");
            e.printStackTrace();
            Socket socket = attemptReconnect();
            if (socket != null){
                getLogger().info("Reconnect successful, attempting to send packet again");
                try {
                    return sendPacket(packet, socket);
                } catch (PacketException e1) {
                    getLogger().warning("Failed to send packet");
                    e1.printStackTrace();
                }
            }
        } catch (PacketException e) {
            getLogger().warning("Failed to send packet");
            e.printStackTrace();
        }
        return null;
    }

    public <R> R sendPacket(Packet<R> packet, Socket socket) throws PacketException {
        if (bungeeMasterSpigotConfig.getPassword().length != 0){
            packet.setPassword(bungeeMasterSpigotConfig.getPassword());
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
        getLogger().warning(String.format("Attempting to reconnect with %d attempts", bungeeMasterSpigotConfig.getReconnectAttempts()));
        int attempts = 0;
        Throwable latestThrowable = null;
        while (attempts <= bungeeMasterSpigotConfig.getReconnectAttempts()) {
            try {
                return connect();
            } catch (IOException e1) {
                latestThrowable = e1;
            }
            attempts++;
        }
        getLogger().warning(String.format("Failed to reconnect to BungeeMaster on BungeeCord after %d attempts", bungeeMasterSpigotConfig.getReconnectAttempts()));
        if (latestThrowable != null){
            latestThrowable.printStackTrace();
        }
        lock();
        return null;
    }

    public void lock() {
        locked = true;
        getLogger().warning("Addon will not work properly until '/bungeemaster reconnect' is ran. To reload the configuration run '/bungeemaster reload'.");
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

    public List<PacketHandler<?>> getPacketHandlers() {
        return packetHandlers;
    }

    public String[] addAddonPrefix(String[] syntaxes) {
        return Arrays.stream(syntaxes).map(syntax -> "[(bm|bungeemaster)] " + syntax).toArray(String[]::new);
    }

    public void registerEffect(Class<? extends Effect> effect){
        if (!effect.isAnnotationPresent(Documentation.class)){
            throw new RegistrationException("Effect class: " + effect.getCanonicalName()
                    + " does not have a Documentation annotation");
        }
        Documentation documentation = effect.getDeclaredAnnotation(Documentation.class);
        Skript.registerEffect(effect, addAddonPrefix(documentation.syntax()));
    }

    public void registerExpression(Class<? extends Expression> expression, ExpressionType expressionType) {
        if (!expression.isAnnotationPresent(Documentation.class)) {
            throw new RegistrationException("Expression class: " + expression.getCanonicalName()
                    + " does not have a Documentation annotation");
        }
        Documentation documentation = expression.getDeclaredAnnotation(Documentation.class);
        Skript.registerExpression(expression, getGenericType(expression, 0),
                expressionType, addAddonPrefix(documentation.syntax()));
    }

    public Class getGenericType(Class clazz, int index) {
        return (Class) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[index];
    }

    public static class RegistrationException extends RuntimeException {
        public RegistrationException() {
        }

        public RegistrationException(String message) {
            super(message);
        }

        public RegistrationException(String message, Throwable cause) {
            super(message, cause);
        }

        public RegistrationException(Throwable cause) {
            super(cause);
        }
    }

    public String[] convertObjectsToNamesAndUUIDs(Object[] objects){
        return Arrays.stream(objects)
                .map(obj -> {
                    if (obj instanceof Player) {
                        //Would use UUID but sometimes Spigot servers don't have true UUID forwarding enabled
                        return ((Player) obj).getName();
                    } else if (obj instanceof String) {
                        return (String) obj;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}

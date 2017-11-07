package me.tonymaster21.bungeemaster.packets;

import java.io.Serializable;

/**
 * @author Andrew Tran
 */
public class BungeeDebugInfo implements Serializable {
    private static final long serialVersionUID = -5816495506828807430L;
    private String os, javaVersion, bungeeVersion, bungeeMasterVersion, plugins;
    private int bungeeMasterPort, playerCount;

    public BungeeDebugInfo(String os, String javaVersion, String bungeeVersion, String bungeeMasterVersion, String plugins, int bungeeMasterPort, int playerCount) {
        this.os = os;
        this.javaVersion = javaVersion;
        this.bungeeVersion = bungeeVersion;
        this.bungeeMasterVersion = bungeeMasterVersion;
        this.plugins = plugins;
        this.bungeeMasterPort = bungeeMasterPort;
        this.playerCount = playerCount;
    }

    public String getOs() {
        return os;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getBungeeVersion() {
        return bungeeVersion;
    }

    public String getBungeeMasterVersion() {
        return bungeeMasterVersion;
    }

    public String getPlugins() {
        return plugins;
    }

    public int getBungeeMasterPort() {
        return bungeeMasterPort;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}

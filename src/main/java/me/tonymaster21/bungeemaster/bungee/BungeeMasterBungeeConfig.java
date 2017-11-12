package me.tonymaster21.bungeemaster.bungee;

import java.net.InetAddress;

/**
 * @author Andrew Tran
 */
public class BungeeMasterBungeeConfig {
    private String host;
    private InetAddress inetAddressHost;
    private int port;
    private char[] password;

    public BungeeMasterBungeeConfig(String host, InetAddress inetAddressHost, int port, char[] password) {
        this.host = host;
        this.inetAddressHost = inetAddressHost;
        this.port = port;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public InetAddress getInetAddressHost() {
        return inetAddressHost;
    }

    public void setInetAddressHost(InetAddress inetAddressHost) {
        this.inetAddressHost = inetAddressHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}

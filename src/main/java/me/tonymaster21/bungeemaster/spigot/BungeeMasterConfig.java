package me.tonymaster21.bungeemaster.spigot;

/**
 * @author Andrew Tran
 */
public class BungeeMasterConfig {
    private String host;
    private int port;
    private char[] password;
    private int heartbeatSeconds;
    private int reconnectAttempts;

    public BungeeMasterConfig(String host, int port, char[] password, int heartbeatSeconds, int reconnectAttempts) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.heartbeatSeconds = heartbeatSeconds;
        this.reconnectAttempts = reconnectAttempts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public int getHeartbeatSeconds() {
        return heartbeatSeconds;
    }

    public void setHeartbeatSeconds(int heartbeatSeconds) {
        this.heartbeatSeconds = heartbeatSeconds;
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    public void setReconnectAttempts(int reconnectAttempts) {
        this.reconnectAttempts = reconnectAttempts;
    }
}

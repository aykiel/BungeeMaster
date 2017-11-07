package me.tonymaster21.bungeemaster.util;

/**
 * @author Andrew Tran
 */
public class PropertyUtil {
    public static String getOS() {
        return System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version");
    }

    public static String getJavaVersion() {
        return System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")";
    }
}

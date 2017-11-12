package me.tonymaster21.bungeemaster.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.net.InetSocketAddress;
import java.util.stream.Collectors;

/**
 * @author Andrew Tran
 */
public class BungeeMasterBungeeCommand extends Command{
    private BungeeMaster bungeeMaster;

    public BungeeMasterBungeeCommand(BungeeMaster bungeeMaster) {
        super("bungeebungeemaster", null, "bbm", "bungeebm", "bbungeem");
        this.bungeeMaster = bungeeMaster;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent(""));
            sender.sendMessage(new ComponentBuilder("BungeeMaster BungeeCord " +
                    bungeeMaster.getDescription().getVersion())
                    .color(ChatColor.GOLD).create());
            sender.sendMessage(new ComponentBuilder("Connected Instances: ")
                    .color(ChatColor.GOLD)
                    .append(bungeeMaster.getServerMap().values()
                            .stream()
                            .map(server -> {
                                InetSocketAddress address = server.getAddress();
                                String stringRepresentation = address.toString();
                                if (stringRepresentation.contains("/")){
                                    stringRepresentation = stringRepresentation.split("/")[1];
                                }
                                return stringRepresentation;
                            })
                            .collect(Collectors.joining(","))
                            )
                    .color(ChatColor.YELLOW)
                    .create());
            sender.sendMessage(new ComponentBuilder("Usage: ")
                    .color(ChatColor.GOLD)
                    .append("/bbm [reload]")
                    .color(ChatColor.YELLOW)
                    .create());
            sender.sendMessage(new TextComponent(""));
            return;
        }
        String argument = args[0];
        if (argument.equals("reload")) {
            bungeeMaster.loadConfig();
            sender.sendMessage(
                    new ComponentBuilder("Reloaded BungeeMaster on BungeeCord configuration successfully")
                    .color(ChatColor.GREEN)
                    .create()
            );
        } else{
            sender.sendMessage(new ComponentBuilder("Invalid argument. Use ").color(ChatColor.RED)
                            .append("/bbm").color(ChatColor.GRAY)
                            .append(" for help.").color(ChatColor.RED).create());
        }
    }
}

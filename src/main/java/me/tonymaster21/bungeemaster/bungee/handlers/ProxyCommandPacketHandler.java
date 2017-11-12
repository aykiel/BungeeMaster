package me.tonymaster21.bungeemaster.bungee.handlers;

import me.tonymaster21.bungeemaster.bungee.BungeeMaster;
import me.tonymaster21.bungeemaster.packets.Result;
import me.tonymaster21.bungeemaster.packets.spigot.ProxyCommandPacket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrew Tran
 */
public class ProxyCommandPacketHandler extends BungeeActionPacketHandler<ProxyCommandPacket> {
    public ProxyCommandPacketHandler(Class<ProxyCommandPacket> packetClass, BungeeMaster bungeeMaster) {
        super(packetClass, bungeeMaster);
    }

    @Override
    public Result handlePacket(ProxyCommandPacket packet, Socket socket) {
        String command = packet.getCommand();
        if (command == null) {
            return getErrorResult("Command is null");
        }
        CommandSender commandSender = getBungeeMaster().getProxy().getConsole();
        if (packet.isOutput()) {
            RecordingCommandSender recordingCommandSender = new RecordingCommandSender(commandSender);
            getBungeeMaster().getProxy().getPluginManager().dispatchCommand(recordingCommandSender, command);
            List<String> messages = recordingCommandSender.getMessages();
            return getSuccessfulResult(messages.toArray());
        } else {
            getBungeeMaster().getProxy().getPluginManager().dispatchCommand(commandSender, command);
        }
        return getSuccessfulResult();
    }

    public static class RecordingCommandSender implements CommandSender {
        private List<String> messages = new ArrayList<>();
        private CommandSender commandSender;

        public RecordingCommandSender(CommandSender commandSender) {
            this.commandSender = commandSender;
        }

        @Override
        public String getName() {
            return commandSender.getName();
        }

        @Override
        public void sendMessage(String message) {
            messages.add(message);
            commandSender.sendMessage(message);
        }

        @Override
        public void sendMessages(String... messages) {
            this.messages.addAll(Arrays.asList(messages));
            commandSender.sendMessages(messages);
        }

        @Override
        public void sendMessage(BaseComponent... message) {
            this.messages.addAll(
                    Arrays.stream(message)
                            .map(baseComponent -> baseComponent.toLegacyText())
                            .collect(Collectors.toList())
            );
            commandSender.sendMessage(message);
        }

        @Override
        public void sendMessage(BaseComponent message) {
            this.messages.add(message.toLegacyText());
            commandSender.sendMessage(message);
        }

        @Override
        public Collection<String> getGroups() {
            return commandSender.getGroups();
        }

        @Override
        public void addGroups(String... groups) {
            commandSender.addGroups(groups);
        }

        @Override
        public void removeGroups(String... groups) {
            commandSender.removeGroups(groups);
        }

        @Override
        public boolean hasPermission(String permission) {
            return commandSender.hasPermission(permission);
        }

        @Override
        public void setPermission(String permission, boolean value) {
            commandSender.setPermission(permission, value);
        }

        @Override
        public Collection<String> getPermissions() {
            return commandSender.getPermissions();
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}

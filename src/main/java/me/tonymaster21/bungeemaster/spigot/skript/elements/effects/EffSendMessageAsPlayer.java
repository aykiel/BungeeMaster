package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.SendMessageAsPlayerPacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMEffect;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Andrew Tran
 */
@Documentation(
        name = "Send Message As Player",
        description = "Force a player to say a message",
        examples = {
                @Example({
                        "on join:",
                        "\tsend bungeecord message \"Hello!\" as player"}
                )
        },
        syntax = {
                "make %strings/%players% (say|send) [bungee][cord] [message][s] %strings%",
                "(send|message) [bungee][cord] [chat] message[s] %strings% (from|as) %strings/players% [on] [bungee][cord]"
        }
)
public class EffSendMessageAsPlayer extends BMEffect{
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffSendMessageAsPlayer.class);
    }
    private Expression<String> messages;
    private Expression<?> senders;

    @Override
    protected void execute(Event event) {
        String[] messages = this.messages.getAll(event);
        Object[] senders = this.senders.getAll(event);
        if (messages == null || senders == null) {
            return;
        }
        Arrays.stream(getBungeeMaster().convertObjectsToNamesAndUUIDs(senders))
                .forEach(sender -> Arrays.stream(messages)
                        .filter(Objects::nonNull)
                        .forEach(message -> send(new SendMessageAsPlayerPacket(sender, message))));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "send bungeecord messages " + messages.toString(event, debug) + " as " + senders.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        senders = expressions[matchedPattern == 0 ? 0 : 1];
        messages = (Expression<String>) expressions[matchedPattern == 0 ? 1 : 0];
        return true;
    }
}

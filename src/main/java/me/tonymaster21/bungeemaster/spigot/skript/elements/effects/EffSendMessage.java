package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.BroadcastMessagePacket;
import me.tonymaster21.bungeemaster.packets.spigot.SendMessagePacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMEffect;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Andrew Tran
 */
@Documentation(
    name = "Send Message",
    description = "Send a message to players on the proxy",
    examples = {
        @Example({
            "send message \"Hello!\" to \"TonyMaster21\" on bungee"
        })
    },
    syntax = {
        "broadcast [on] bungee[cord] [message][s] %strings%",
        "broadcast [message][s] %strings% on bungee[cord]",
        "(send|message) [on] bungee[cord] [message][s] %strings% to [player][s] %strings/players%",
        "(send|message) [message][s] %strings% to [player][s] %strings/players% on bungee[cord]"
    }
)
public class EffSendMessage extends BMEffect{
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffSendMessage.class);
    }
    private Expression<?> players;
    private Expression<String> messages;
    private boolean broadcast;

    @Override
    protected void execute(Event event) {
        String[] messages = this.messages.getAll(event);
        if (messages == null) {
            return;
        }
        if (broadcast) {
            send(new BroadcastMessagePacket(messages));
        } else {
            Object[] players = this.players.getAll(event);
            if (players == null) {
                return;
            }
            Arrays.stream(players)
                    .filter(Objects::nonNull)
                    .map(object -> object instanceof Player ? ((Player) object).getName() : object.toString())
                    .forEach(player -> send(new SendMessagePacket(player, messages)));
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return broadcast ? "broadcast " + messages.toString(event, debug) + " on bungeecord" :
                "message " + messages.toString(event, debug) + " to " + players.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        broadcast = matchedPattern == 0 || matchedPattern == 1;
        messages = (Expression<String>) expressions[0];
        if (!broadcast) {
            players = expressions[1];
        }
        return true;
    }
}

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
        name = "Perform Command As Player",
        description = "Force a player to run a command",
        examples = {
                @Example({
                        "make all players run command \"glist\" on bungee"
                })
        },
        syntax = {
                "make %strings/players% (run|execute|perform) [command][s] %strings% on bungee[cord]",
                "(run|execute|perform) [bungee][cord] [command][s] %strings% (from|as) %strings/players% on bungee[cord]"
        }
)
public class EffPerformCommandAsPlayer extends BMEffect{
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffPerformCommandAsPlayer.class);
    }

    private Expression<String> commands;
    private Expression<?> senders;

    @Override
    protected void execute(Event event) {
        String[] commands = this.commands.getAll(event);
        Object[] senders = this.senders.getAll(event);
        if (commands == null || senders == null) {
            return;
        }
        Arrays.stream(getBungeeMaster().convertObjectsToNamesAndUUIDs(senders))
                .forEach(sender ->
                        Arrays.stream(commands)
                        .filter(Objects::nonNull)
                        .forEach(command -> {
                            if (command.startsWith("/")) {
                                command = command.substring(1);
                            }
                            send(new SendMessageAsPlayerPacket(sender, "/" + command));
                        })
                );
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "make " + senders.toString(event, debug) +
                " execute command" + (senders.isSingle() ? "" : "s")
                + commands.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        senders = expressions[matchedPattern == 0 ? 0 : 1];
        commands = (Expression<String>) expressions[matchedPattern == 0 ? 1 : 0];
        return true;
    }
}

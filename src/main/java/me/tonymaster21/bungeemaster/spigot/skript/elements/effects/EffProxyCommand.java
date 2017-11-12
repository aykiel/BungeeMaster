package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.ProxyCommandPacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMEffect;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * @author Andrew Tran
 */
@Documentation(
        name = "Run Proxy Command",
        description = "Run a command on the proxy",
        examples = {
                @Example({
                        "run bungee command \"alert \"\"Hello everyone!\"\"\""
                })
        },
        syntax = {
                "(make|force) bungee[cord] [(run|execute)] [command][s] %strings%",
                "(run|execute) [command][s] %strings% on bungee[cord]",
                "(run|execute) bungee[cord] [command][s] %strings%",
                "(make|force) bungee[cord] [(run|execute)] [command][s] %strings% with output",
                "(run|execute) [command][s] %strings% on bungee[cord] with output",
                "(run|execute) bungee[cord] [command][s] %strings% with output",

        }
)
public class EffProxyCommand extends BMEffect{
    private static String[] lastOutput;
    static  {
        BungeeMaster.getBungeeMaster().registerEffect(EffProxyCommand.class);
    }

    private Expression<String> commands;
    private boolean output;

    @Override
    protected void execute(Event event) {
        String[] commands = this.commands.getAll(event);
        if (commands == null) {
            return;
        }
        Arrays.stream(commands).forEach(command -> {
            Object rawOutput = send(new ProxyCommandPacket(command, this.output));
            if (this.output) {
                lastOutput = Arrays.stream((Object[]) rawOutput).toArray(String[]::new);
            }
        });
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "make bungee run command" +
                (commands.isSingle() ? "" : "s")
                + " " + commands.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        output = matchedPattern == 3 || matchedPattern == 4 || matchedPattern == 5;
        commands = (Expression<String>) expressions[0];
        return true;
    }

    public static String[] getLastOutput() {
        return lastOutput;
    }
}

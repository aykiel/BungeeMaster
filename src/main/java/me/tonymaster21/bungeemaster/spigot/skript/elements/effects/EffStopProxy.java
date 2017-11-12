package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.StopProxyPacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMEffect;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import org.bukkit.event.Event;

/**
 * @author Andrew Tran
 */
@Documentation(
        name = "Stop Proxy",
        description = "Stop the proxy",
        examples = {
                @Example({
                        "stop the proxy"
                })
        },
        syntax = {
                "stop [the] bungee[cord] [proxy] [(due to|for) [with] [(message|reason)] %-string%]",
                "stop [the] proxy [(due to|for) [with] [(message|reason)] %-string%]"
        }
)
public class EffStopProxy extends BMEffect{
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffStopProxy.class);
    }

    private Expression<String> reason;

    @Override
    protected void execute(Event event) {
        send(new StopProxyPacket(reason == null ? null : reason.getSingle(event)));
    }

    @Override
    public String toString(Event event, boolean b) {
        return "stop the proxy";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        reason = (Expression<String>) expressions[0];
        return true;
    }
}

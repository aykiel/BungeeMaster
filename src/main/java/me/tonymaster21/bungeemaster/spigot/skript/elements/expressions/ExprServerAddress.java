package me.tonymaster21.bungeemaster.spigot.skript.elements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.RetrieveServerAddressPacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMExpression;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import org.bukkit.event.Event;

/**
 * @author Andrew Tran
 */
@Documentation(
        name = "Server Address",
        description = "The address of a server on the proxy",
        examples = {
                @Example({
                        "set {_address} to the ip address of server \"hub\""
                })
        },
        syntax = {
                "(ip|[ip] address) of [bungee][cord] [the] server %string%",
                "[bungee][cord] server %string%'s (ip|[ip] address)"
        }
)
public class ExprServerAddress extends BMExpression<String>{
    static {
        BungeeMaster.getBungeeMaster().registerExpression(ExprServerAddress.class, ExpressionType.PROPERTY);
    }

    private Expression<String> server;

    @Override
    protected String[] get(Event event) {
        String server = this.server.getSingle(event);
        if (server == null) {
            return null;
        }
        return new String[]{(String) send(new RetrieveServerAddressPacket(server))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "address of bungee server " + server.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        server = (Expression<String>) expressions[0];
        return true;
    }
}

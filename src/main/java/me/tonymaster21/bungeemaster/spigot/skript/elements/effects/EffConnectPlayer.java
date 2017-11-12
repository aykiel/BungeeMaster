package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.ConnectPlayerPacket;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMEffect;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Examples;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Syntax;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Andrew Tran
 */
@Documentation(name = "Connect Player", description = "Connect a player to a server")
@Examples(
    @Example({
        "connect all players to \"trollmc.org\""
    })
)
@Syntax({"connect %strings/players% to [local] [server] %string%",
        "connect %strings/players% to [remote] (ip|address) %string%"})
public class EffConnectPlayer extends BMEffect {
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffConnectPlayer.class);
    }
    private Expression<?> players;
    private Expression<String> destination;
    private boolean remote;

    @Override
    protected void execute(Event event) {
        Object[] players = this.players.getAll(event);
        String destination = this.destination.getSingle(event);
        if (players == null || destination == null) {
            return;
        }
        Arrays.stream(players)
            .filter(Objects::nonNull)
            .map(object -> object instanceof Player ? ((Player) object).getName() : object.toString())
            .forEach(player -> send(new ConnectPlayerPacket(player, destination, remote)));
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "connect " + players.toString(event, debug) + " to " + (remote ? "server" : "ip") + " " + destination.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.players = expressions[0];
        this.destination = (Expression<String>) expressions[1];
        this.remote = matchedPattern == 1;
        return true;
    }
}

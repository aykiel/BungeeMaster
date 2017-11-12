package me.tonymaster21.bungeemaster.spigot.skript.elements.effects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.packets.spigot.KickAllPlayersPacket;
import me.tonymaster21.bungeemaster.packets.spigot.KickPlayerPacket;
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
    name = "Kick Player",
    description = "Kick a player from the proxy",
    examples = {
        @Example({
            "kick bungee player due to \"Turn off your l33t hacks\""
        })
    },
    syntax = {
        "kick (all bungee[cord] players|everyone) [due to %-strings%]",
        "kick bungee[cord] [player][s] %strings/players% [due to %-strings%]"
    }
)
public class EffKickPlayer extends BMEffect{
    static {
        BungeeMaster.getBungeeMaster().registerEffect(EffKickPlayer.class);
    }
    private Expression<?> players;
    private Expression<String> reason;
    private boolean all;

    @Override
    protected void execute(Event event) {
        String reason = this.reason == null ? null : String.join("\n", this.reason.getAll(event));
        if (all) {
            send(new KickAllPlayersPacket(reason));
        } else {
            Object[] players = this.players.getAll(event);
            if (players == null) {
                return;
            }
            Arrays.stream(getBungeeMaster().convertObjectsToNamesAndUUIDs(players))
                    .forEach(player -> send(new KickPlayerPacket(player, reason)));
        }
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "kick " +
                (all ? "all bungee players" : players.toString(event, debug))
                + (reason == null ? "" : " due to " + reason.toString(event, debug));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.all = matchedPattern == 0;
        if (!all) {
            this.players = expressions[0];
        }
        this.reason = (Expression<String>) expressions[matchedPattern == 0 ? 0 : 1];
        return true;
    }
}

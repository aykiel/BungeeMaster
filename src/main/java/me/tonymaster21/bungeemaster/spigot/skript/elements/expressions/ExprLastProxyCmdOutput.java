package me.tonymaster21.bungeemaster.spigot.skript.elements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import me.tonymaster21.bungeemaster.spigot.BungeeMaster;
import me.tonymaster21.bungeemaster.spigot.skript.BMExpession;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Documentation;
import me.tonymaster21.bungeemaster.spigot.skript.annotations.Example;
import me.tonymaster21.bungeemaster.spigot.skript.elements.effects.EffProxyCommand;
import org.bukkit.event.Event;

/**
 * @author Andrew Tran
 */
@Documentation(
        name = "Last Proxy Command Output",
        description = "The output of the last ran proxy command",
        examples = {
                @Example({
                        "run bungee command \"glist\" with output",
                        "set {output::*} to last bungee command output"
                })
        },
        syntax = {
                "last [(ran|executed)] bungee[cord] (command|cmd) output"
        }
)
public class ExprLastProxyCmdOutput extends BMExpession<String>{
    static {
        BungeeMaster.getBungeeMaster().registerExpression(ExprLastProxyCmdOutput.class,
                ExpressionType.SIMPLE);
    }

    @Override
    protected String[] get(Event event) {
        return EffProxyCommand.getLastOutput();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "last bungee command output";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}

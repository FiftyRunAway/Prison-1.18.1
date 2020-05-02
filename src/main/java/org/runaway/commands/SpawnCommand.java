package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;

import java.util.Collections;

/*
 * Created by _RunAway_ on 21.4.2019
 */


public class SpawnCommand extends CommandManager {

    public SpawnCommand() {
        super("spawn", "prison.commands", Collections.singletonList("спавн"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = Main.gamers.get(p.getUniqueId());
        gamer.teleport(Main.SPAWN);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}

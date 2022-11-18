package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.managers.GamerManager;

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
        Gamer gamer = GamerManager.getGamer(p);
        gamer.teleport(Prison.SPAWN.add(0, 1, 0));
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}

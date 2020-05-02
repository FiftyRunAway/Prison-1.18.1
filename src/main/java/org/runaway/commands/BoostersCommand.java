package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.BoostersMenu;

import java.util.Collections;

/*
 * Created by _RunAway_ on 1.2.2019
 */

public class BoostersCommand extends CommandManager {

    public BoostersCommand() {
        super("boosters", "prison.commands", Collections.singletonList("бустеры"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new BoostersMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}

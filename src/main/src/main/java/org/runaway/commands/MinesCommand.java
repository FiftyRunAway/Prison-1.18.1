package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.MinesMenu;

import java.util.Arrays;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class MinesCommand extends CommandManager {

    public MinesCommand() {
        super("mines", "prison.commands", Arrays.asList("шахты", "mine"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new MinesMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}

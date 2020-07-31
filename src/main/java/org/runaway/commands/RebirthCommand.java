package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.RebirthMenu;

import java.util.Collections;

/*
 * Created by _RunAway_ on 13.5.2019
 */

public class RebirthCommand extends CommandManager {

    public RebirthCommand() {
        super("rebirth", "prison.commands", Collections.singletonList("престиж"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        p.openInventory(RebirthMenu.getMenu(p));
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
